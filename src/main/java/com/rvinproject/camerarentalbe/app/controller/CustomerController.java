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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/api/customers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(null, body), "Berhasil membuat customer"));
    }

    @PostMapping(value = "/api/customers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCustomerMultipart(@RequestParam("name") String name,
                                                     @RequestParam("phone") String phone,
                                                     @RequestParam("address") String address,
                                                     @RequestParam("identity_type") String identityType,
                                                     @RequestParam("identity_number") String identityNumber,
                                                     @RequestParam(value = "identity_image", required = false) MultipartFile identityImage) {
        CustomerRequest body = customerRequest(name, phone, address, identityType, identityNumber, identityImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(null, body), "Berhasil membuat customer"));
    }

    @PutMapping(value = "/api/customers/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(id, body), "Berhasil mengubah customer"));
    }

    @PutMapping(value = "/api/customers/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCustomerMultipart(@PathVariable Long id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("phone") String phone,
                                                     @RequestParam("address") String address,
                                                     @RequestParam("identity_type") String identityType,
                                                     @RequestParam("identity_number") String identityNumber,
                                                     @RequestParam(value = "identity_image", required = false) MultipartFile identityImage,
                                                     HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        CustomerRequest body = customerRequest(name, phone, address, identityType, identityNumber, identityImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(id, body), "Berhasil mengubah customer"));
    }

    @DeleteMapping("/api/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCustomer(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus customer"));
    }

    private CustomerRequest customerRequest(String name, String phone, String address, String identityType,
                                            String identityNumber, MultipartFile identityImage) {
        CustomerRequest request = new CustomerRequest();
        request.setName(name);
        request.setPhone(phone);
        request.setAddress(address);
        request.setIdentityType(identityType);
        request.setIdentityNumber(identityNumber);
        request.setIdentityImageUpload(identityImage);
        return request;
    }
}
