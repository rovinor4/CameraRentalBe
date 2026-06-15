package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.service.ReportService;
import com.rvinproject.camerarentalbe.app.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/api/reports/rentals.csv")
    public ResponseEntity<String> rentals(@RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return csv("rentals.csv", reportService.rentals(startDate, endDate));
    }

    @GetMapping("/api/reports/returns.csv")
    public ResponseEntity<String> returns(@RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return csv("returns.csv", reportService.returns(startDate, endDate));
    }

    @GetMapping("/api/reports/penalties.csv")
    public ResponseEntity<String> penalties(@RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return csv("penalties.csv", reportService.penalties(startDate, endDate));
    }

    @GetMapping("/api/reports/customers.csv")
    public ResponseEntity<String> customers() {
        return csv("customers.csv", reportService.customers());
    }

    @GetMapping("/api/reports/items.csv")
    public ResponseEntity<String> items(HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        AuthUtil.requireSuperAdmin(admin);
        return csv("items.csv", reportService.items());
    }

    @GetMapping("/api/reports/payments.csv")
    public ResponseEntity<String> payments(@RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                           HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return csv("payments.csv", reportService.payments(startDate, endDate));
    }

    private ResponseEntity<String> csv(String filename, String body) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(body);
    }
}
