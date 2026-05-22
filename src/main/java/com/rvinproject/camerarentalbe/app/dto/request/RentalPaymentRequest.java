package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentalPaymentRequest {
    @NotNull
    private Long rentalId;
    @NotNull
    private Long paymentMethodId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    @NotBlank
    private String status;
    private String proofImage;
}
