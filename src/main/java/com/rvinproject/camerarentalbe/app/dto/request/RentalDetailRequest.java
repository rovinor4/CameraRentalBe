package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalDetailRequest {
    private Long itemId;
    private Long itemStatusId;
    @Min(1)
    private Integer quantity;
}
