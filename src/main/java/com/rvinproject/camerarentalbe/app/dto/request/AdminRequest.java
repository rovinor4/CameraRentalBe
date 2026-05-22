package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    private String password;
    @NotBlank
    private String role;
}
