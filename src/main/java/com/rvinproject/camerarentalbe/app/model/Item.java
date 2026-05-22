package com.rvinproject.camerarentalbe.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_detail_id")
    private CategoryDetail categoryDetail;
    @Column(nullable = false)
    private String name;
    private String brand;
    private String model;
    @Column(name = "serial_number", unique = true)
    private String serialNumber;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "daily_price", nullable = false)
    private BigDecimal dailyPrice;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private String status;
    private String image;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
