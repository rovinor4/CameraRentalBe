package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.ItemStatusRequest;
import com.rvinproject.camerarentalbe.app.model.ItemStatusRecord;
import com.rvinproject.camerarentalbe.app.service.ItemStatusService;
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
public class ItemStatusController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "item.name", "item.brand", "item.model", "status", "createdAt", "updatedAt");
    private final ItemStatusService service;

    @GetMapping("/api/item-statuses")
    public ResponseEntity<?> itemStatuses(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.itemStatuses(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil item status"));
    }

    @GetMapping("/api/item-statuses/{id}")
    public ResponseEntity<?> itemStatus(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.itemStatus(id), "Berhasil mengambil item status"));
    }

    @PostMapping("/api/items/{itemId}/item-statuses")
    public ResponseEntity<?> createItemStatus(@PathVariable Long itemId, @RequestBody ItemStatusRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.createItemStatus(itemId, body), "Berhasil membuat item status"));
    }

    @PutMapping("/api/item-statuses/{id}")
    public ResponseEntity<?> updateItemStatus(@PathVariable Long id, @Valid @RequestBody ItemStatusRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.updateItemStatus(id, body), "Berhasil mengubah item status"));
    }

    @PatchMapping("/api/item-statuses/{id}")
    public ResponseEntity<?> patchItemStatus(@PathVariable Long id, @Valid @RequestBody ItemStatusRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        ItemStatusRecord itemStatus = service.updateItemStatus(id, body);
        return ResponseEntity.ok(JSONFormat.success(itemStatus, "Berhasil mengubah item status"));
    }

    @DeleteMapping("/api/item-statuses/{id}")
    public ResponseEntity<?> deleteItemStatus(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteItemStatus(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus item status"));
    }
}
