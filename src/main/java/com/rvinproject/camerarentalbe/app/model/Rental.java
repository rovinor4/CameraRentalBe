package com.rvinproject.camerarentalbe.app.model;

import com.rvinproject.camerarentalbe.app.enumModel.RentalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
    @Column(name = "rental_code", nullable = false, unique = true)
    private String rentalCode;
    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;
    @Column(name = "planned_return_date", nullable = false)
    private LocalDate plannedReturnDate;
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalStatus status;
    @Column(columnDefinition = "TEXT")
    private String note;
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalDetail> details = new ArrayList<>();
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
