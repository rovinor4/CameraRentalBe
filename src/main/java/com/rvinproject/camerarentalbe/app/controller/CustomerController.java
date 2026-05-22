package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.CustomerRequest;
import com.rvinproject.camerarentalbe.app.service.CustomerService;
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
public class CustomerController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "name", "phone", "address", "identityType", "identityNumber", "identityImage", "createdAt", "updatedAt");
    private final CustomerService service;

    @GetMapping("/api/customers")
    public ResponseEntity<?> customers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.customers(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil customer"));
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<?> customer(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.customer(id), "Berhasil mengambil customer"));
    }

    @PostMapping("/api/customers")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(null, body), "Berhasil membuat customer"));
    }

    @PutMapping("/api/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(id, body), "Berhasil mengubah customer"));
    }

    @DeleteMapping("/api/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCustomer(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus customer"));
    }
}
