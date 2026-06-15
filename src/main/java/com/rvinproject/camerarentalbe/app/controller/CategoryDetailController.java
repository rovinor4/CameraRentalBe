package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.CategoryDetailRequest;
import com.rvinproject.camerarentalbe.app.service.CategoryDetailService;
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
public class CategoryDetailController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "category.name", "name", "description", "createdAt", "updatedAt");
    private final CategoryDetailService service;

    @GetMapping("/api/category-details")
    public ResponseEntity<?> categoryDetails(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.categoryDetails(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil category detail"));
    }

    @GetMapping("/api/category-details/{id}")
    public ResponseEntity<?> categoryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.categoryDetail(id), "Berhasil mengambil category detail"));
    }

    @GetMapping("/api/categories-detail/get/{categoryId}")
    public ResponseEntity<?> categoryDetailsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(JSONFormat.success(service.categoryDetailsByCategory(categoryId), "Berhasil mengambil category detail berdasarkan category"));
    }

    @PostMapping("/api/categories-detail/create/{categoryId}")
    public ResponseEntity<?> createCategoryDetailByCategory(@PathVariable Long categoryId,
                                                            @Valid @RequestBody CategoryDetailRequest body,
                                                            HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategoryDetail(null, categoryId, body), "Berhasil membuat category detail"));
    }

    @PutMapping("/api/categories-detail/update/{categoryId}/{categoryDetailId}")
    public ResponseEntity<?> updateCategoryDetailByCategory(@PathVariable Long categoryId,
                                                            @PathVariable Long categoryDetailId,
                                                            @Valid @RequestBody CategoryDetailRequest body,
                                                            HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.updateCategoryDetailByCategory(categoryId, categoryDetailId, body), "Berhasil mengubah category detail"));
    }

    @DeleteMapping("/api/categories-detail/delete/{categoryId}/{categoryDetailId}")
    public ResponseEntity<?> deleteCategoryDetailByCategory(@PathVariable Long categoryId,
                                                            @PathVariable Long categoryDetailId,
                                                            HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCategoryDetailByCategory(categoryId, categoryDetailId);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus category detail"));
    }

    @PostMapping("/api/category-details")
    public ResponseEntity<?> createCategoryDetail(@Valid @RequestBody CategoryDetailRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategoryDetail(null, body), "Berhasil membuat category detail"));
    }

    @PutMapping("/api/category-details/{id}")
    public ResponseEntity<?> updateCategoryDetail(@PathVariable Long id, @Valid @RequestBody CategoryDetailRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategoryDetail(id, body), "Berhasil mengubah category detail"));
    }

    @DeleteMapping("/api/category-details/{id}")
    public ResponseEntity<?> deleteCategoryDetail(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCategoryDetail(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus category detail"));
    }
}
