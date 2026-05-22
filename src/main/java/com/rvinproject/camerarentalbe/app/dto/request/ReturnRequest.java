package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReturnRequest {
    @NotNull
    private Long rentalId;
    @NotNull
    private LocalDate returnDate;
    private String conditionNote;
    @NotNull
    private Boolean hasPenalty;
    private Long penaltyPaymentMethodId;
}
