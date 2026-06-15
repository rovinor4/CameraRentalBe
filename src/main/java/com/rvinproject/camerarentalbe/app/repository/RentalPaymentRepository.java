package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long>, JpaSpecificationExecutor<RentalPayment> {
    List<RentalPayment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<RentalPayment> findByRentalId(Long rentalId);

    List<RentalPayment> findByPenaltyId(Long penaltyId);

    @Query("select coalesce(sum(payment.amount), 0) from RentalPayment payment where payment.status = com.rvinproject.camerarentalbe.app.enumModel.PaymentStatus.paid")
    BigDecimal sumPaidAmount();
}
