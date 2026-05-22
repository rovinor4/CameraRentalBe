package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.MaintenanceRequest;
import com.rvinproject.camerarentalbe.app.service.ItemMaintenanceService;
import com.rvinproject.camerarentalbe.app.util.AuthUtil;
import com.rvinproject.camerarentalbe.app.util.PageUtil;
import com.rvinproject.camerarentalbe.app.util.PageableUtil;
import com.rvinproject.camerarentalbe.app.util.QueryUtil;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ItemMaintenanceController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "item.name", "admin.name", "title", "description", "maintenanceDate", "cost", "status", "createdAt", "updatedAt");
    private final ItemMaintenanceService service;

    @GetMapping("/api/maintenance")
    public ResponseEntity<?> maintenances(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.maintenances(
                QueryUtil.specification(params, SEARCH_FIELDS, "maintenanceDate"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil maintenance"));
    }

    @GetMapping("/api/maintenance/{id}")
    public ResponseEntity<?> maintenance(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.maintenance(id), "Berhasil mengambil maintenance"));
    }

    @PostMapping("/api/maintenance")
    public ResponseEntity<?> createMaintenance(@Valid @RequestBody MaintenanceRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveMaintenance(null, body, AuthUtil.admin(request)), "Berhasil membuat maintenance"));
    }

    @PutMapping("/api/maintenance/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Long id, @Valid @RequestBody MaintenanceRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveMaintenance(id, body, AuthUtil.admin(request)), "Berhasil mengubah maintenance"));
    }

    @DeleteMapping("/api/maintenance/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteMaintenance(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus maintenance"));
    }
}
