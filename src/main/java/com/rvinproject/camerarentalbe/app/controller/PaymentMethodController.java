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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentMethodController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "name", "type", "contentType", "contentValue", "imageUpload", "active", "createdAt", "updatedAt");
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

    @PostMapping(value = "/api/payment-methods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(null, body), "Berhasil membuat payment method"));
    }

    @PostMapping(value = "/api/payment-methods", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaymentMethodMultipart(@RequestParam("name") String name,
                                                          @RequestParam("type") String type,
                                                          @RequestParam("content_type") String contentType,
                                                          @RequestParam(value = "content_value", required = false) String contentValue,
                                                          @RequestParam(value = "image_upload", required = false) MultipartFile imageUpload,
                                                          @RequestParam("active") Boolean active,
                                                          HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        PaymentMethodRequest body = paymentMethodRequest(name, type, contentType, contentValue, imageUpload, active);
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(null, body), "Berhasil membuat payment method"));
    }

    @PutMapping(value = "/api/payment-methods/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(id, body), "Berhasil mengubah payment method"));
    }

    @PutMapping(value = "/api/payment-methods/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePaymentMethodMultipart(@PathVariable Long id,
                                                          @RequestParam("name") String name,
                                                          @RequestParam("type") String type,
                                                          @RequestParam("content_type") String contentType,
                                                          @RequestParam(value = "content_value", required = false) String contentValue,
                                                          @RequestParam(value = "image_upload", required = false) MultipartFile imageUpload,
                                                          @RequestParam("active") Boolean active,
                                                          HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        PaymentMethodRequest body = paymentMethodRequest(name, type, contentType, contentValue, imageUpload, active);
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(id, body), "Berhasil mengubah payment method"));
    }

    @DeleteMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deletePaymentMethod(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus payment method"));
    }

    private PaymentMethodRequest paymentMethodRequest(String name, String type, String contentType, String contentValue,
                                                      MultipartFile imageUpload, Boolean active) {
        PaymentMethodRequest request = new PaymentMethodRequest();
        request.setName(name);
        request.setType(type);
        request.setContentType(contentType);
        request.setContentValue(contentValue);
        request.setImageUpload(imageUpload);
        request.setActive(active);
        return request;
    }
}
