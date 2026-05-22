package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.CategoryRequest;
import com.rvinproject.camerarentalbe.app.service.CategoryService;
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
public class CategoryController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "name", "description", "createdAt", "updatedAt");
    private final CategoryService service;

    @GetMapping("/api/categories")
    public ResponseEntity<?> categories(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.categories(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil category"));
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<?> category(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.category(id), "Berhasil mengambil category"));
    }

    @PostMapping("/api/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategory(null, body), "Berhasil membuat category"));
    }

    @PutMapping("/api/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveCategory(id, body), "Berhasil mengubah category"));
    }

    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteCategory(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus category"));
    }
}
