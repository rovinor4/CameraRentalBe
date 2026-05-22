package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.PaymentMethodRequest;
import com.rvinproject.camerarentalbe.app.enumModel.AdminRole;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.service.PaymentMethodService;
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
public class PaymentMethodController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "name", "type", "contentType", "contentValue", "active", "createdAt", "updatedAt");
    private final PaymentMethodService service;

    @GetMapping("/api/payment-methods")
    public ResponseEntity<?> paymentMethods(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        boolean activeOnly = !AdminRole.super_admin.equals(admin.getRole());
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.paymentMethods(
                activeOnly,
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil payment method"));
    }

    @GetMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> paymentMethod(@PathVariable Long id, HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        if (!AdminRole.super_admin.equals(admin.getRole())) {
            return ResponseEntity.ok(JSONFormat.success(service.activePaymentMethod(id), "Berhasil mengambil payment method"));
        }
        return ResponseEntity.ok(JSONFormat.success(service.paymentMethod(id), "Berhasil mengambil payment method"));
    }

    @PostMapping("/api/payment-methods")
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(null, body), "Berhasil membuat payment method"));
    }

    @PutMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(id, body), "Berhasil mengubah payment method"));
    }

    @DeleteMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deletePaymentMethod(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus payment method"));
    }
}
