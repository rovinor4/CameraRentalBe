package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RentalService {
    private static final Set<String> RENTAL_STATUS = Set.of("pending", "ongoing", "returned", "cancelled");
    private static final Set<String> PAYMENT_STATUS = Set.of("pending", "paid", "failed");
    private static final Set<String> PENALTY_TYPE = Set.of("late_return", "damage", "lost_item", "other");
    private static final Set<String> PENALTY_STATUS = Set.of("unpaid", "paid");
    private static final Set<String> MAINTENANCE_STATUS = Set.of("in_progress", "completed");

    private final RentalRepository rentalRepository;
    private final RentalPaymentRepository rentalPaymentRepository;
    private final RentalReturnRepository rentalReturnRepository;
    private final PenaltyRepository penaltyRepository;
    private final ItemMaintenanceRepository itemMaintenanceRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final MasterDataService masterDataService;

    public Page<Rental> rentals(Specification<Rental> specification, Pageable pageable) {
        return rentalRepository.findAll(specification, pageable);
    }

    public Rental rental(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> notFound("rental"));
    }

    @Transactional
    public Rental createRental(ApiRequest.RentalRequest request, Admin admin) {
        validateRentalRequest(request);
        Rental rental = new Rental();
        rental.setCustomer(customer(request.getCustomerId()));
        rental.setAdmin(admin);
        rental.setRentalCode("RNT-" + StringHelper.RandomString(12));
        rental.setRentalDate(request.getRentalDate());
        rental.setPlannedReturnDate(request.getPlannedReturnDate());
        rental.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "pending" : request.getStatus());
        rental.setNote(request.getNote());

        long days = Math.max(1, ChronoUnit.DAYS.between(request.getRentalDate(), request.getPlannedReturnDate()));
        BigDecimal total = BigDecimal.ZERO;
        for (ApiRequest.RentalDetailRequest detailRequest : request.getDetails()) {
            Item item = item(detailRequest.getItemId());
            validateRentableItem(item, detailRequest.getQuantity());
            item.setStock(item.getStock() - detailRequest.getQuantity());
            if (item.getStock() == 0) {
                item.setStatus("rented");
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
    public Rental updateRental(Long id, ApiRequest.RentalRequest request) {
        validateRentalRequest(request);
        Rental rental = rental(id);
        if ("returned".equals(rental.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental sudah returned");
        }
        restoreStock(rental);
        rental.getDetails().clear();
        rental.setCustomer(customer(request.getCustomerId()));
        rental.setRentalDate(request.getRentalDate());
        rental.setPlannedReturnDate(request.getPlannedReturnDate());
        rental.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? rental.getStatus() : request.getStatus());
        rental.setNote(request.getNote());

        long days = Math.max(1, ChronoUnit.DAYS.between(request.getRentalDate(), request.getPlannedReturnDate()));
        BigDecimal total = BigDecimal.ZERO;
        for (ApiRequest.RentalDetailRequest detailRequest : request.getDetails()) {
            Item item = item(detailRequest.getItemId());
            validateRentableItem(item, detailRequest.getQuantity());
            item.setStock(item.getStock() - detailRequest.getQuantity());
            if (item.getStock() == 0) {
                item.setStatus("rented");
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
        if (!"cancelled".equals(rental.getStatus()) && !"returned".equals(rental.getStatus())) {
            restoreStock(rental);
        }
        rentalRepository.delete(rental);
    }

    public Page<RentalPayment> rentalPayments(Specification<RentalPayment> specification, Pageable pageable) {
        return rentalPaymentRepository.findAll(specification, pageable);
    }

    public RentalPayment rentalPayment(Long id) {
        return rentalPaymentRepository.findById(id).orElseThrow(() -> notFound("rental payment"));
    }

    public RentalPayment saveRentalPayment(Long id, ApiRequest.RentalPaymentRequest request) {
        ValidationUtil.oneOf(request.getStatus(), PAYMENT_STATUS, "status");
        RentalPayment payment = id == null ? new RentalPayment() : rentalPayment(id);
        payment.setRental(rental(request.getRentalId()));
        payment.setPaymentMethod(masterDataService.activePaymentMethod(request.getPaymentMethodId()));
        if (payment.getPaymentCode() == null) {
            payment.setPaymentCode("PAY-" + StringHelper.RandomString(12));
        }
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(request.getStatus());
        payment.setProofImage(request.getProofImage());
        return rentalPaymentRepository.save(payment);
    }

    public void deleteRentalPayment(Long id) {
        rentalPaymentRepository.delete(rentalPayment(id));
    }

    public Page<RentalReturn> returns(Specification<RentalReturn> specification, Pageable pageable) {
        return rentalReturnRepository.findAll(specification, pageable);
    }

    public RentalReturn rentalReturn(Long id) {
        return rentalReturnRepository.findById(id).orElseThrow(() -> notFound("return"));
    }

    @Transactional
    public RentalReturn saveReturn(Long id, ApiRequest.ReturnRequest request, Admin admin) {
        RentalReturn rentalReturn = id == null ? new RentalReturn() : rentalReturn(id);
        Rental rental = rental(request.getRentalId());
        if (id == null && rentalReturnRepository.existsByRentalId(rental.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental sudah memiliki return");
        }
        if ("cancelled".equals(rental.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental cancelled tidak bisa direturn");
        }
        rentalReturn.setRental(rental);
        rentalReturn.setAdmin(admin);
        rentalReturn.setReturnDate(request.getReturnDate());
        rentalReturn.setConditionNote(request.getConditionNote());
        rentalReturn.setHasPenalty(request.getHasPenalty());
        rentalReturn.setPenaltyPaymentMethod(request.getPenaltyPaymentMethodId() == null ? null : masterDataService.activePaymentMethod(request.getPenaltyPaymentMethodId()));
        rental.setActualReturnDate(request.getReturnDate());
        rental.setStatus("returned");
        restoreStock(rental);
        return rentalReturnRepository.save(rentalReturn);
    }

    public void deleteReturn(Long id) {
        rentalReturnRepository.delete(rentalReturn(id));
    }

    public Page<Penalty> penalties(Specification<Penalty> specification, Pageable pageable) {
        return penaltyRepository.findAll(specification, pageable);
    }

    public Penalty penalty(Long id) {
        return penaltyRepository.findById(id).orElseThrow(() -> notFound("denda"));
    }

    public Penalty savePenalty(Long id, ApiRequest.PenaltyRequest request) {
        ValidationUtil.oneOf(request.getPenaltyType(), PENALTY_TYPE, "penaltyType");
        ValidationUtil.oneOf(request.getStatus(), PENALTY_STATUS, "status");
        Penalty penalty = id == null ? new Penalty() : penalty(id);
        penalty.setRentalReturn(rentalReturn(request.getReturnId()));
        penalty.setPenaltyType(request.getPenaltyType());
        penalty.setDescription(request.getDescription());
        penalty.setAmount(request.getAmount());
        penalty.setStatus(request.getStatus());
        penalty.setPaidAt("paid".equals(request.getStatus()) && request.getPaidAt() == null ? java.time.LocalDateTime.now() : request.getPaidAt());
        return penaltyRepository.save(penalty);
    }

    public void deletePenalty(Long id) {
        penaltyRepository.delete(penalty(id));
    }

    public Page<ItemMaintenance> maintenances(Specification<ItemMaintenance> specification, Pageable pageable) {
        return itemMaintenanceRepository.findAll(specification, pageable);
    }

    public ItemMaintenance maintenance(Long id) {
        return itemMaintenanceRepository.findById(id).orElseThrow(() -> notFound("maintenance"));
    }

    @Transactional
    public ItemMaintenance saveMaintenance(Long id, ApiRequest.MaintenanceRequest request, Admin admin) {
        ValidationUtil.oneOf(request.getStatus(), MAINTENANCE_STATUS, "status");
        ItemMaintenance maintenance = id == null ? new ItemMaintenance() : maintenance(id);
        Item item = item(request.getItemId());
        maintenance.setItem(item);
        maintenance.setAdmin(admin);
        maintenance.setTitle(request.getTitle());
        maintenance.setDescription(request.getDescription());
        maintenance.setMaintenanceDate(request.getMaintenanceDate());
        maintenance.setCost(request.getCost());
        maintenance.setStatus(request.getStatus());
        item.setStatus("in_progress".equals(request.getStatus()) ? "maintenance" : (item.getStock() > 0 ? "available" : "inactive"));
        return itemMaintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        itemMaintenanceRepository.delete(maintenance(id));
    }

    private void validateRentalRequest(ApiRequest.RentalRequest request) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            ValidationUtil.oneOf(request.getStatus(), RENTAL_STATUS, "status");
        }
        if (request.getPlannedReturnDate().isBefore(request.getRentalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "plannedReturnDate harus setelah rentalDate");
        }
    }

    private void validateRentableItem(Item item, Integer quantity) {
        if (!"available".equals(item.getStatus()) && !"rented".equals(item.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item tidak tersedia: " + item.getName());
        }
        if (item.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stok item tidak cukup: " + item.getName());
        }
    }

    private void restoreStock(Rental rental) {
        for (RentalDetail detail : rental.getDetails()) {
            Item item = detail.getItem();
            item.setStock(item.getStock() + detail.getQuantity());
            if (!"maintenance".equals(item.getStatus()) && !"inactive".equals(item.getStatus())) {
                item.setStatus("available");
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
