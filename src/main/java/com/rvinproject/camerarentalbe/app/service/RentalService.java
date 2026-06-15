package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.RentalDetailRequest;
import com.rvinproject.camerarentalbe.app.dto.request.RentalRequest;
import com.rvinproject.camerarentalbe.app.enumModel.ItemStatus;
import com.rvinproject.camerarentalbe.app.enumModel.RentalStatus;
import com.rvinproject.camerarentalbe.app.model.*;
import com.rvinproject.camerarentalbe.app.repository.*;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import com.rvinproject.camerarentalbe.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final ItemStatusService itemStatusService;
    private final RentalDetailRepository rentalDetailRepository;

    public Page<Rental> rentals(Specification<Rental> specification, Pageable pageable) {
        return rentalRepository.findAll(specification, pageable);
    }

    public Rental rental(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> notFound("rental"));
    }

    public List<Rental> rentalNotReturned() {
        return rentalRepository.findByStatus(RentalStatus.ongoing);
    }

    @Transactional
    public Rental createRental(RentalRequest request, Admin admin) {
        validateRentalRequest(request);
        Rental rental = new Rental();
        rental.setCustomer(customer(request.getCustomerId()));
        rental.setAdmin(admin);
        rental.setRentalCode("RNT-" + StringHelper.RandomString(12));
        rental.setRentalDate(request.getRentalDate());
        rental.setPlannedReturnDate(request.getPlannedReturnDate());
        rental.setStatus(request.getStatus() == null || request.getStatus().isBlank()
                ? RentalStatus.pending
                : ValidationUtil.enumValue(request.getStatus(), RentalStatus.class, "status"));
        rental.setNote(request.getNote());

        long days = Math.max(1, ChronoUnit.DAYS.between(request.getRentalDate(), request.getPlannedReturnDate()));
        BigDecimal total = BigDecimal.ZERO;
        for (ItemStatusRecord itemStatus : requestedItemStatuses(request.getDetails())) {
            RentalDetail detail = buildDetail(rental, itemStatus, days);
            rental.getDetails().add(detail);
            total = total.add(detail.getSubtotal());
        }
        rental.setTotalPrice(total);
        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental updateRental(Long id, RentalRequest request) {
        validateRentalRequest(request);
        Rental rental = rental(id);
        if (RentalStatus.returned.equals(rental.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental sudah returned");
        }
        restoreStock(rental);
        rental.getDetails().clear();
        rental.setCustomer(customer(request.getCustomerId()));
        rental.setRentalDate(request.getRentalDate());
        rental.setPlannedReturnDate(request.getPlannedReturnDate());
        rental.setStatus(request.getStatus() == null || request.getStatus().isBlank()
                ? rental.getStatus()
                : ValidationUtil.enumValue(request.getStatus(), RentalStatus.class, "status"));
        rental.setNote(request.getNote());

        long days = Math.max(1, ChronoUnit.DAYS.between(request.getRentalDate(), request.getPlannedReturnDate()));
        BigDecimal total = BigDecimal.ZERO;
        for (ItemStatusRecord itemStatus : requestedItemStatuses(request.getDetails())) {
            RentalDetail detail = buildDetail(rental, itemStatus, days);
            rental.getDetails().add(detail);
            total = total.add(detail.getSubtotal());
        }
        rental.setTotalPrice(total);
        return rentalRepository.save(rental);
    }

    @Transactional
    public void deleteRental(Long id) {
        Rental rental = rental(id);
        if (!RentalStatus.cancelled.equals(rental.getStatus()) && !RentalStatus.returned.equals(rental.getStatus())) {
            restoreStock(rental);
        }
        rentalRepository.delete(rental);
    }

    @Transactional
    public void deleteRentalDetail(Long id) {
        RentalDetail detail = rentalDetailRepository.findById(id).orElseThrow(() -> notFound("rental detail"));
        Rental rental = detail.getRental();
        detail.getItemStatus().setStatus(ItemStatus.available);
        rental.getDetails().remove(detail);
        rentalDetailRepository.delete(detail);
        recalculateRentalTotal(rental);
        rentalRepository.save(rental);
    }

    private void validateRentalRequest(RentalRequest request) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            ValidationUtil.enumValue(request.getStatus(), RentalStatus.class, "status");
        }
        if (request.getPlannedReturnDate().isBefore(request.getRentalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "plannedReturnDate harus setelah rentalDate");
        }
    }

    private List<ItemStatusRecord> requestedItemStatuses(List<RentalDetailRequest> detailRequests) {
        List<ItemStatusRecord> itemStatuses = new ArrayList<>();
        Set<Long> selectedIds = new HashSet<>();
        for (RentalDetailRequest detailRequest : detailRequests) {
            if (detailRequest.getItemStatusId() != null) {
                ItemStatusRecord itemStatus = itemStatusService.itemStatus(detailRequest.getItemStatusId());
                if (!selectedIds.add(itemStatus.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item status tidak boleh duplikat: " + itemStatus.getId());
                }
                validateRentableItemStatus(itemStatus);
                itemStatuses.add(itemStatus);
                continue;
            }
            if (detailRequest.getItemId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item_status_id wajib diisi");
            }
            int quantity = detailRequest.getQuantity() == null ? 1 : detailRequest.getQuantity();
            item(detailRequest.getItemId());
            List<ItemStatusRecord> availableStatuses = itemStatusService.availableItemStatuses(detailRequest.getItemId(), quantity);
            for (ItemStatusRecord itemStatus : availableStatuses) {
                if (!selectedIds.add(itemStatus.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item status tidak boleh duplikat: " + itemStatus.getId());
                }
            }
            itemStatuses.addAll(availableStatuses);
        }
        return itemStatuses;
    }

    private void validateRentableItemStatus(ItemStatusRecord itemStatus) {
        if (!ItemStatus.available.equals(itemStatus.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item status tidak tersedia: " + itemStatus.getId());
        }
    }

    public void restoreStock(Rental rental) {
        for (RentalDetail detail : rental.getDetails()) {
            detail.getItemStatus().setStatus(ItemStatus.available);
        }
    }

    private RentalDetail buildDetail(Rental rental, ItemStatusRecord itemStatus, long days) {
        Item item = itemStatus.getItem();
        itemStatus.setStatus(ItemStatus.rented);
        RentalDetail detail = new RentalDetail();
        detail.setRental(rental);
        detail.setItemStatus(itemStatus);
        detail.setDailyPrice(item.getDailyPrice());
        detail.setQuantity(1);
        detail.setSubtotal(item.getDailyPrice().multiply(BigDecimal.valueOf(days)));
        return detail;
    }

    private void recalculateRentalTotal(Rental rental) {
        BigDecimal total = rental.getDetails().stream()
                .map(RentalDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        rental.setTotalPrice(total);
    }

    private Customer customer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> notFound("customer"));
    }

    private Item item(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> notFound("item"));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
