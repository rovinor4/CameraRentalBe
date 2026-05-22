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
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    public Page<Rental> rentals(Specification<Rental> specification, Pageable pageable) {
        return rentalRepository.findAll(specification, pageable);
    }

    public Rental rental(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> notFound("rental"));
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
        for (RentalDetailRequest detailRequest : request.getDetails()) {
            Item item = item(detailRequest.getItemId());
            validateRentableItem(item, detailRequest.getQuantity());
            item.setStock(item.getStock() - detailRequest.getQuantity());
            if (item.getStock() == 0) {
                item.setStatus(ItemStatus.rented);
            }
            RentalDetail detail = new RentalDetail();
            detail.setRental(rental);
            detail.setItem(item);
            detail.setDailyPrice(item.getDailyPrice());
            detail.setQuantity(detailRequest.getQuantity());
            detail.setSubtotal(item.getDailyPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity())).multiply(BigDecimal.valueOf(days)));
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
        for (RentalDetailRequest detailRequest : request.getDetails()) {
            Item item = item(detailRequest.getItemId());
            validateRentableItem(item, detailRequest.getQuantity());
            item.setStock(item.getStock() - detailRequest.getQuantity());
            if (item.getStock() == 0) {
                item.setStatus(ItemStatus.rented);
            }
            RentalDetail detail = new RentalDetail();
            detail.setRental(rental);
            detail.setItem(item);
            detail.setDailyPrice(item.getDailyPrice());
            detail.setQuantity(detailRequest.getQuantity());
            detail.setSubtotal(item.getDailyPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity())).multiply(BigDecimal.valueOf(days)));
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

    private void validateRentalRequest(RentalRequest request) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            ValidationUtil.enumValue(request.getStatus(), RentalStatus.class, "status");
        }
        if (request.getPlannedReturnDate().isBefore(request.getRentalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "plannedReturnDate harus setelah rentalDate");
        }
    }

    private void validateRentableItem(Item item, Integer quantity) {
        if (!ItemStatus.available.equals(item.getStatus()) && !ItemStatus.rented.equals(item.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item tidak tersedia: " + item.getName());
        }
        if (item.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stok item tidak cukup: " + item.getName());
        }
    }

    public void restoreStock(Rental rental) {
        for (RentalDetail detail : rental.getDetails()) {
            Item item = detail.getItem();
            item.setStock(item.getStock() + detail.getQuantity());
            if (!ItemStatus.maintenance.equals(item.getStatus()) && !ItemStatus.inactive.equals(item.getStatus())) {
                item.setStatus(ItemStatus.available);
            }
        }
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
