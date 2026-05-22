package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MaintenanceRequest {
    @NotNull
    private Long itemId;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDate maintenanceDate;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cost;
    @NotBlank
    private String status;
}
