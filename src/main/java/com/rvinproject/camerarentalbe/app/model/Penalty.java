package com.rvinproject.camerarentalbe.app.model;

import com.rvinproject.camerarentalbe.app.enumModel.PenaltyStatus;
import com.rvinproject.camerarentalbe.app.enumModel.PenaltyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
