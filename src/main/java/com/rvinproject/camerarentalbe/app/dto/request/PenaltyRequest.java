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
public class PenaltyRequest {
    @NotNull
    private Long returnId;
    @NotBlank
    private String penaltyType;
    private String description;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
    @NotBlank
    private String status;
    private LocalDateTime paidAt;
}
