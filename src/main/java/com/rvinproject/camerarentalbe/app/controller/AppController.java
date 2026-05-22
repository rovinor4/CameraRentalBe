package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.helper.JSONFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AppController {
    @GetMapping("/api")
    public ResponseEntity<?> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app_name", "Camera Rental");
        response.put("version", "1.0.00");
        return ResponseEntity.ok(JSONFormat.success(response, "Berhasil mengambil informasi aplikasi"));
    }
}
