package com.rvinproject.camerarentalbe.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AppController {
    @GetMapping("/api")
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app_name", "Camera Rental");
        response.put("version", "1.0.00");
        return response;
    }
}