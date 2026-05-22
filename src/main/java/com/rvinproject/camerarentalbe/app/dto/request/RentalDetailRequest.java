package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalDetailRequest {
    @NotNull
    private Long itemId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
