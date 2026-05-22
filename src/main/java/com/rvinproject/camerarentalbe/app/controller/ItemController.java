package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.ItemRequest;
import com.rvinproject.camerarentalbe.app.service.ItemService;
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
public class ItemController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "category.name", "categoryDetail.name", "name", "brand", "model", "serialNumber", "description", "dailyPrice", "stock", "status", "image", "createdAt", "updatedAt");
    private final ItemService service;

    @GetMapping("/api/items")
    public ResponseEntity<?> items(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.items(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil item"));
    }

    @GetMapping("/api/items/{id}")
    public ResponseEntity<?> item(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.item(id), "Berhasil mengambil item"));
    }

    @PostMapping("/api/items")
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(null, body), "Berhasil membuat item"));
    }

    @PutMapping("/api/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(id, body), "Berhasil mengubah item"));
    }

    @DeleteMapping("/api/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteItem(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus item"));
    }
}
