package com.rvinproject.camerarentalbe.app.request.AuthRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String password;

    @JsonProperty("password_confirmation")
    private String passwordConfirmation;
}