package com.rvinproject.camerarentalbe.app.model;

import com.rvinproject.camerarentalbe.app.enumModel.IdentityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    @Column(name = "identity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentityType identityType;
    @Column(name = "identity_number", nullable = false, unique = true)
    private String identityNumber;
    @Column(name = "identity_image")
    private String identityImage;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
