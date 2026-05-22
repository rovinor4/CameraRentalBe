package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.PenaltyRequest;
import com.rvinproject.camerarentalbe.app.service.PenaltyService;
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
public class PenaltyController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "rentalReturn.rental.rentalCode", "penaltyType", "description", "amount", "status", "paidAt", "createdAt", "updatedAt");
    private final PenaltyService service;

    @GetMapping("/api/denda")
    public ResponseEntity<?> penalties(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.penalties(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "createdAt"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil denda"));
    }

    @GetMapping("/api/denda/{id}")
    public ResponseEntity<?> penalty(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.penalty(id), "Berhasil mengambil denda"));
    }

    @PostMapping("/api/denda")
    public ResponseEntity<?> createPenalty(@Valid @RequestBody PenaltyRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.savePenalty(null, body), "Berhasil membuat denda"));
    }

    @PutMapping("/api/denda/{id}")
    public ResponseEntity<?> updatePenalty(@PathVariable Long id, @Valid @RequestBody PenaltyRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.savePenalty(id, body), "Berhasil mengubah denda"));
    }

    @DeleteMapping("/api/denda/{id}")
    public ResponseEntity<?> deletePenalty(@PathVariable Long id) {
        service.deletePenalty(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus denda"));
    }
}
