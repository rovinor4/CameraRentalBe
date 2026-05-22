package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotBlank
    private String identityType;
    @NotBlank
    private String identityNumber;
    private String identityImage;
}
