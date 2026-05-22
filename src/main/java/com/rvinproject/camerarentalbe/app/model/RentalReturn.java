package com.rvinproject.camerarentalbe.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "returns")
public class RentalReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private Rental rental;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;
    @Column(name = "condition_note", columnDefinition = "TEXT")
    private String conditionNote;
    @Column(name = "has_penalty", nullable = false)
    private Boolean hasPenalty;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_payment_method_id")
    private PaymentMethod penaltyPaymentMethod;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
