package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long>, JpaSpecificationExecutor<RentalPayment> {
    List<RentalPayment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
