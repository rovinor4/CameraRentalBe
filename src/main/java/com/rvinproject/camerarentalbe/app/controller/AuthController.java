package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
import com.rvinproject.camerarentalbe.app.service.AdminAuthService;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ApiRequest.Login request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(JSONFormat.success(adminAuthService.login(request, httpRequest), "Login berhasil"));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminAuthService.logout(authorization);
        return ResponseEntity.ok(JSONFormat.success(null, "Logout berhasil"));
    }
}
