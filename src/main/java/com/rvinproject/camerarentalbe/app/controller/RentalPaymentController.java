package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.RentalPaymentRequest;
import com.rvinproject.camerarentalbe.app.service.RentalPaymentService;
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
public class RentalPaymentController {
    private static final List<String> SEARCH_FIELDS = List.of("id", "rental.rentalCode", "paymentMethod.name", "paymentCode", "amount", "paymentDate", "status", "proofImage", "createdAt", "updatedAt");
    private final RentalPaymentService service;

    @GetMapping("/api/rental-payments")
    public ResponseEntity<?> rentalPayments(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.rentalPayments(
                QueryUtil.dateTimeSpecification(params, SEARCH_FIELDS, "paymentDate"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil rental payment"));
    }

    @GetMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> rentalPayment(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalPayment(id), "Berhasil mengambil rental payment"));
    }

    @PostMapping("/api/rental-payments")
    public ResponseEntity<?> createRentalPayment(@Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PutMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> updateRentalPayment(@PathVariable Long id, @Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @DeleteMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> deleteRentalPayment(@PathVariable Long id) {
        service.deleteRentalPayment(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental payment"));
    }
}
