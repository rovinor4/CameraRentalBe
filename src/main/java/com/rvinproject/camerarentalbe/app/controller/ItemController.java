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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "category.name", "categoryDetail.name", "name", "brand", "model", "serialNumber", "description", "dailyPrice", "stock", "image", "createdAt", "updatedAt");
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

    @GetMapping("/api/items/available")
    public ResponseEntity<?> availableItems(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.availableItems(PageableUtil.from(page, size, Map.of()))), "Berhasil mengambil item available"));
    }

    @GetMapping("/api/items/{itemId}/available-item-status")
    public ResponseEntity<?> availableItemStatus(@PathVariable Long itemId) {
        return ResponseEntity.ok(JSONFormat.success(service.availableItemStatus(itemId), "Berhasil mengambil item status available"));
    }

    @PostMapping(value = "/api/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(null, body), "Berhasil membuat item"));
    }

    @PostMapping(value = "/api/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createItemMultipart(@RequestParam("category_id") Long categoryId,
                                                 @RequestParam(value = "category_detail_id", required = false) Long categoryDetailId,
                                                 @RequestParam("name") String name,
                                                 @RequestParam(value = "brand", required = false) String brand,
                                                 @RequestParam(value = "model", required = false) String model,
                                                 @RequestParam(value = "serial_number", required = false) String serialNumber,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam("daily_price") BigDecimal dailyPrice,
                                                 @RequestParam("stock") Integer stock,
                                                 @RequestParam(value = "status", required = false) String status,
                                                 @RequestParam(value = "image_upload", required = false) MultipartFile imageUpload,
                                                 HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        ItemRequest body = itemRequest(categoryId, categoryDetailId, name, brand, model, serialNumber, description, dailyPrice, stock, status, imageUpload);
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(null, body), "Berhasil membuat item"));
    }

    @PutMapping(value = "/api/items/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(id, body), "Berhasil mengubah item"));
    }

    @PutMapping(value = "/api/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateItemMultipart(@PathVariable Long id,
                                                 @RequestParam("category_id") Long categoryId,
                                                 @RequestParam(value = "category_detail_id", required = false) Long categoryDetailId,
                                                 @RequestParam("name") String name,
                                                 @RequestParam(value = "brand", required = false) String brand,
                                                 @RequestParam(value = "model", required = false) String model,
                                                 @RequestParam(value = "serial_number", required = false) String serialNumber,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam("daily_price") BigDecimal dailyPrice,
                                                 @RequestParam(value = "stock", required = false) Integer stock,
                                                 @RequestParam(value = "status", required = false) String status,
                                                 @RequestParam(value = "image_upload", required = false) MultipartFile imageUpload,
                                                 HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        ItemRequest body = itemRequest(categoryId, categoryDetailId, name, brand, model, serialNumber, description, dailyPrice, stock, status, imageUpload);
        return ResponseEntity.ok(JSONFormat.success(service.saveItem(id, body), "Berhasil mengubah item"));
    }

    @DeleteMapping("/api/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteItem(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus item"));
    }

    private ItemRequest itemRequest(Long categoryId, Long categoryDetailId, String name, String brand, String model,
                                    String serialNumber, String description, BigDecimal dailyPrice, Integer stock,
                                    String status, MultipartFile imageUpload) {
        ItemRequest request = new ItemRequest();
        request.setCategoryId(categoryId);
        request.setCategoryDetailId(categoryDetailId);
        request.setName(name);
        request.setBrand(brand);
        request.setModel(model);
        request.setSerialNumber(serialNumber);
        request.setDescription(description);
        request.setDailyPrice(dailyPrice);
        request.setStock(stock);
        request.setStatus(status);
        request.setImageUpload(imageUpload);
        return request;
    }
}
