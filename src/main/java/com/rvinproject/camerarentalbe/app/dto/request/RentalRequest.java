package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RentalRequest {
    @NotNull
    private Long customerId;
    @NotNull
    private LocalDate rentalDate;
    @NotNull
    private LocalDate plannedReturnDate;
    private String status;
    private String note;
    @NotEmpty
    @Valid
    private List<RentalDetailRequest> details;
}
