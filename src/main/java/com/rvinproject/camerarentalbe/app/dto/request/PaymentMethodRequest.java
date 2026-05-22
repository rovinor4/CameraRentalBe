package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotBlank
    private String contentType;
    private String contentValue;
    @NotNull
    private Boolean active;
}
