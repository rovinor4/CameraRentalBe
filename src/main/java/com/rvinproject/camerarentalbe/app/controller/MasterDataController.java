package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.service.MasterDataService;
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
@RequiredArgsConstructor
public class MasterDataController {
    private final MasterDataService service;
    private static final List<String> CUSTOMER_SEARCH_FIELDS = List.of("id", "name", "phone", "address", "identityType", "identityNumber", "identityImage", "createdAt", "updatedAt");
    private static final List<String> CATEGORY_SEARCH_FIELDS = List.of("id", "name", "description", "createdAt", "updatedAt");
    private static final List<String> CATEGORY_DETAIL_SEARCH_FIELDS = List.of("id", "category.name", "name", "description", "createdAt", "updatedAt");
    private static final List<String> ITEM_SEARCH_FIELDS = List.of("id", "category.name", "categoryDetail.name", "name", "brand", "model", "serialNumber", "description", "dailyPrice", "stock", "status", "image", "createdAt", "updatedAt");
    private static final List<String> PAYMENT_METHOD_SEARCH_FIELDS = List.of("id", "name", "type", "contentType", "contentValue", "active", "createdAt", "updatedAt");

    @GetMapping("/api/customers")
    public ResponseEntity<?> customers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.customers(
                QueryUtil.dateTimeSpecification(params, CUSTOMER_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil customer"));
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<?> customer(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.customer(id), "Berhasil mengambil customer"));
    }

    @PostMapping("/api/customers")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody ApiRequest.CustomerRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(null, body), "Berhasil membuat customer"));
    }

    @PutMapping("/api/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody ApiRequest.CustomerRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCustomer(id, body), "Berhasil mengubah customer"));
    }

    @DeleteMapping("/api/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCustomer(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus customer"));
    }

    @GetMapping("/api/categories")
    public ResponseEntity<?> categories(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.categories(
                QueryUtil.dateTimeSpecification(params, CATEGORY_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil category"));
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<?> category(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.category(id), "Berhasil mengambil category"));
    }

    @PostMapping("/api/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody ApiRequest.CategoryRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategory(null, body), "Berhasil membuat category"));
    }

    @PutMapping("/api/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody ApiRequest.CategoryRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategory(id, body), "Berhasil mengubah category"));
    }

    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCategory(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus category"));
    }

    @GetMapping("/api/category-details")
    public ResponseEntity<?> categoryDetails(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.categoryDetails(
                QueryUtil.dateTimeSpecification(params, CATEGORY_DETAIL_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil category detail"));
    }

    @GetMapping("/api/category-details/{id}")
    public ResponseEntity<?> categoryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.categoryDetail(id), "Berhasil mengambil category detail"));
    }

    @PostMapping("/api/category-details")
    public ResponseEntity<?> createCategoryDetail(@Valid @RequestBody ApiRequest.CategoryDetailRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategoryDetail(null, body), "Berhasil membuat category detail"));
    }

    @PutMapping("/api/category-details/{id}")
    public ResponseEntity<?> updateCategoryDetail(@PathVariable Long id, @Valid @RequestBody ApiRequest.CategoryDetailRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategoryDetail(id, body), "Berhasil mengubah category detail"));
    }

    @DeleteMapping("/api/category-details/{id}")
    public ResponseEntity<?> deleteCategoryDetail(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCategoryDetail(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus category detail"));
    }

    @GetMapping("/api/items")
    public ResponseEntity<?> items(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.items(
                QueryUtil.dateTimeSpecification(params, ITEM_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil item"));
    }

    @GetMapping("/api/items/{id}")
    public ResponseEntity<?> item(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.item(id), "Berhasil mengambil item"));
    }

    @PostMapping("/api/items")
    public ResponseEntity<?> createItem(@Valid @RequestBody ApiRequest.ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(null, body), "Berhasil membuat item"));
    }

    @PutMapping("/api/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody ApiRequest.ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(id, body), "Berhasil mengubah item"));
    }

    @DeleteMapping("/api/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteItem(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus item"));
    }

    @GetMapping("/api/payment-methods")
    public ResponseEntity<?> paymentMethods(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        boolean activeOnly = !"super_admin".equals(admin.getRole());
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.paymentMethods(
                activeOnly,
                QueryUtil.dateTimeSpecification(params, PAYMENT_METHOD_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil payment method"));
    }

    @GetMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> paymentMethod(@PathVariable Long id, HttpServletRequest request) {
        Admin admin = AuthUtil.admin(request);
        if (!"super_admin".equals(admin.getRole())) {
            return ResponseEntity.ok(JSONFormat.success(service.activePaymentMethod(id), "Berhasil mengambil payment method"));
        }
        return ResponseEntity.ok(JSONFormat.success(service.paymentMethod(id), "Berhasil mengambil payment method"));
    }

    @PostMapping("/api/payment-methods")
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody ApiRequest.PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(null, body), "Berhasil membuat payment method"));
    }

    @PutMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody ApiRequest.PaymentMethodRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.savePaymentMethod(id, body), "Berhasil mengubah payment method"));
    }

    @DeleteMapping("/api/payment-methods/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deletePaymentMethod(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus payment method"));
    }

    private Pageable pageable(int page, int size, Map<String, String[]> params) {
        Sort.Direction direction = "asc".equalsIgnoreCase(QueryUtil.direction(params)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 100)), Sort.by(direction, QueryUtil.sortField(params)));
    }
}
