package com.rvinproject.camerarentalbe.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String image;
    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemStatusRecord> itemStatuses = new ArrayList<>();
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Transient
    public long getAvailableCount() {
        return countStatus(com.rvinproject.camerarentalbe.app.enumModel.ItemStatus.available);
    }

    @Transient
    public long getRentedCount() {
        return countStatus(com.rvinproject.camerarentalbe.app.enumModel.ItemStatus.rented);
    }

    @Transient
    public long getMaintenanceCount() {
        return countStatus(com.rvinproject.camerarentalbe.app.enumModel.ItemStatus.maintenance);
    }

    @Transient
    public long getInactiveCount() {
        return countStatus(com.rvinproject.camerarentalbe.app.enumModel.ItemStatus.inactive);
    }

    private long countStatus(com.rvinproject.camerarentalbe.app.enumModel.ItemStatus status) {
        return itemStatuses.stream().filter(itemStatus -> status.equals(itemStatus.getStatus())).count();
    }
}
