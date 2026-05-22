package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.ReturnRequest;
import com.rvinproject.camerarentalbe.app.service.RentalReturnService;
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
public class RentalReturnController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "rental.rentalCode", "admin.name", "returnDate", "conditionNote", "hasPenalty", "penaltyPaymentMethod.name", "createdAt", "updatedAt");
    private final RentalReturnService service;

    @GetMapping("/api/returns")
    public ResponseEntity<?> returns(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.returns(
                QueryUtil.specification(params, SEARCH_FIELDS, "returnDate"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil return"));
    }

    @GetMapping("/api/returns/{id}")
    public ResponseEntity<?> rentalReturn(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalReturn(id), "Berhasil mengambil return"));
    }

    @PostMapping("/api/returns")
    public ResponseEntity<?> createReturn(@Valid @RequestBody ReturnRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.saveReturn(null, body, AuthUtil.admin(request)), "Berhasil membuat return"));
    }

    @PutMapping("/api/returns/{id}")
    public ResponseEntity<?> updateReturn(@PathVariable Long id, @Valid @RequestBody ReturnRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.saveReturn(id, body, AuthUtil.admin(request)), "Berhasil mengubah return"));
    }

    @DeleteMapping("/api/returns/{id}")
    public ResponseEntity<?> deleteReturn(@PathVariable Long id) {
        service.deleteReturn(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus return"));
    }
}
