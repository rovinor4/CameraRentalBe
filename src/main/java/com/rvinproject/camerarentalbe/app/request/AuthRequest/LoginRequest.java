package com.rvinproject.camerarentalbe.app.request.AuthRequest;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}