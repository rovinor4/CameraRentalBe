package com.rvinproject.camerarentalbe.app.model;

import com.rvinproject.camerarentalbe.app.enumModel.PaymentContentType;
import com.rvinproject.camerarentalbe.app.enumModel.PaymentMethodType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentContentType contentType;
    @Column(name = "content_value", columnDefinition = "TEXT")
    private String contentValue;
    @Column(name = "image_upload")
    private String imageUpload;
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
