package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.model.*;
import com.rvinproject.camerarentalbe.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final RentalRepository rentalRepository;
    private final RentalReturnRepository rentalReturnRepository;
    private final PenaltyRepository penaltyRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final RentalPaymentRepository rentalPaymentRepository;

    public String rentals(LocalDate startDate, LocalDate endDate) {
        List<Rental> rows = startDate == null || endDate == null ? rentalRepository.findAll() : rentalRepository.findByRentalDateBetween(startDate, endDate);
        StringBuilder csv = new StringBuilder("id,rental_code,customer,admin,rental_date,planned_return_date,actual_return_date,total_price,status\n");
        for (Rental row : rows) {
            csv.append(row.getId()).append(',').append(esc(row.getRentalCode())).append(',').append(esc(row.getCustomer().getName())).append(',')
                    .append(esc(row.getAdmin().getName())).append(',').append(row.getRentalDate()).append(',').append(row.getPlannedReturnDate()).append(',')
                    .append(row.getActualReturnDate()).append(',').append(row.getTotalPrice()).append(',').append(row.getStatus()).append('\n');
        }
        return csv.toString();
    }

    public String returns(LocalDate startDate, LocalDate endDate) {
        List<RentalReturn> rows = startDate == null || endDate == null ? rentalReturnRepository.findAll() : rentalReturnRepository.findByReturnDateBetween(startDate, endDate);
        StringBuilder csv = new StringBuilder("id,rental_code,admin,return_date,has_penalty,condition_note\n");
        for (RentalReturn row : rows) {
            csv.append(row.getId()).append(',').append(esc(row.getRental().getRentalCode())).append(',').append(esc(row.getAdmin().getName())).append(',')
                    .append(row.getReturnDate()).append(',').append(row.getHasPenalty()).append(',').append(esc(row.getConditionNote())).append('\n');
        }
        return csv.toString();
    }

    public String penalties(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.atTime(LocalTime.MAX);
        List<Penalty> rows = start == null || end == null ? penaltyRepository.findAll() : penaltyRepository.findByCreatedAtBetween(start, end);
        StringBuilder csv = new StringBuilder("id,return_id,penalty_type,description,amount,status,paid_at\n");
        for (Penalty row : rows) {
            csv.append(row.getId()).append(',').append(row.getRentalReturn().getId()).append(',').append(row.getPenaltyType()).append(',')
                    .append(esc(row.getDescription())).append(',').append(row.getAmount()).append(',').append(row.getStatus()).append(',').append(row.getPaidAt()).append('\n');
        }
        return csv.toString();
    }

    public String customers() {
        StringBuilder csv = new StringBuilder("id,name,phone,address,identity_type,identity_number,identity_image\n");
        for (Customer row : customerRepository.findAll()) {
            csv.append(row.getId()).append(',').append(esc(row.getName())).append(',').append(esc(row.getPhone())).append(',')
                    .append(esc(row.getAddress())).append(',').append(row.getIdentityType()).append(',').append(esc(row.getIdentityNumber())).append(',')
                    .append(esc(row.getIdentityImage())).append('\n');
        }
        return csv.toString();
    }

    public String items() {
        StringBuilder csv = new StringBuilder("id,name,category,category_detail,brand,model,serial_number,daily_price,stock,status\n");
        for (Item row : itemRepository.findAll()) {
            csv.append(row.getId()).append(',').append(esc(row.getName())).append(',').append(esc(row.getCategory().getName())).append(',')
                    .append(esc(row.getCategoryDetail() == null ? null : row.getCategoryDetail().getName())).append(',').append(esc(row.getBrand())).append(',')
                    .append(esc(row.getModel())).append(',').append(esc(row.getSerialNumber())).append(',').append(row.getDailyPrice()).append(',')
                    .append(row.getStock()).append(',').append(row.getStatus()).append('\n');
        }
        return csv.toString();
    }

    public String payments(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.atTime(LocalTime.MAX);
        List<RentalPayment> rows = start == null || end == null ? rentalPaymentRepository.findAll() : rentalPaymentRepository.findByCreatedAtBetween(start, end);
        StringBuilder csv = new StringBuilder("id,payment_code,rental_code,payment_method,amount,payment_date,status,proof_image\n");
        for (RentalPayment row : rows) {
            csv.append(row.getId()).append(',').append(esc(row.getPaymentCode())).append(',').append(esc(row.getRental().getRentalCode())).append(',')
                    .append(esc(row.getPaymentMethod().getName())).append(',').append(row.getAmount()).append(',').append(row.getPaymentDate()).append(',')
                    .append(row.getStatus()).append(',').append(esc(row.getProofImage())).append('\n');
        }
        return csv.toString();
    }

    private String esc(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
