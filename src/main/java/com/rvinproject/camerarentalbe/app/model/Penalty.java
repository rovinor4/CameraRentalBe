package com.rvinproject.camerarentalbe.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rvinproject.camerarentalbe.app.enumModel.PenaltyStatus;
import com.rvinproject.camerarentalbe.app.enumModel.PenaltyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "penalties")
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    private RentalReturn rentalReturn;
    @Column(name = "penalty_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PenaltyType penaltyType;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PenaltyStatus status;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    @JsonIgnore
    @OneToMany(mappedBy = "penalty")
    private List<RentalPayment> payments = new ArrayList<>();
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Transient
    public BigDecimal getTotal() {
        return amount;
    }

    @Transient
    public BigDecimal getTotalPaid() {
        return payments.stream()
                .filter(payment -> com.rvinproject.camerarentalbe.app.enumModel.PaymentStatus.paid.equals(payment.getStatus()))
                .map(RentalPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getRemainingPayment() {
        BigDecimal remaining = amount.subtract(getTotalPaid());
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    @Transient
    public BigDecimal getBalanceDue() {
        return getRemainingPayment();
    }
}
