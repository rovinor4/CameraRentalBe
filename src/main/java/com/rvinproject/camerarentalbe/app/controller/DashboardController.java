package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.service.DashboardService;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(JSONFormat.success(dashboardService.summary(), "Berhasil mengambil dashboard"));
    }
}
