package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.service.AdminAuthService;
import com.rvinproject.camerarentalbe.app.util.AuthUtil;
import com.rvinproject.camerarentalbe.app.util.PageUtil;
import com.rvinproject.camerarentalbe.app.util.QueryUtil;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminAuthService adminAuthService;
    private static final List<String> ADMIN_SEARCH_FIELDS = List.of("id", "name", "email", "role", "createdAt", "updatedAt");

    @GetMapping
    public ResponseEntity<?> index(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        AuthUtil.requireSuperAdmin(admin);
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(adminAuthService.allAdmins(
                QueryUtil.dateTimeSpecification(params, ADMIN_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil admin"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(adminAuthService.findAdmin(id), "Berhasil mengambil admin"));
    }

    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody ApiRequest.AdminRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(adminAuthService.createAdmin(body), "Berhasil membuat admin"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ApiRequest.AdminRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(adminAuthService.updateAdmin(id, body), "Berhasil mengubah admin"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        adminAuthService.deleteAdmin(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus admin"));
    }

    private Pageable pageable(int page, int size, Map<String, String[]> params) {
        Sort.Direction direction = "asc".equalsIgnoreCase(QueryUtil.direction(params)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 100)), Sort.by(direction, QueryUtil.sortField(params)));
    }
}
