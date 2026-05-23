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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @PostMapping(value = "/api/rental-payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRentalPayment(@Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PostMapping(value = "/api/rental-payments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRentalPaymentMultipart(@RequestParam("rental_id") Long rentalId,
                                                          @RequestParam("payment_method_id") Long paymentMethodId,
                                                          @RequestParam("amount") BigDecimal amount,
                                                          @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                          @RequestParam("status") String status,
                                                          @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(rentalId, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PutMapping(value = "/api/rental-payments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRentalPayment(@PathVariable Long id, @Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @PutMapping(value = "/api/rental-payments/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRentalPaymentMultipart(@PathVariable Long id,
                                                          @RequestParam("rental_id") Long rentalId,
                                                          @RequestParam("payment_method_id") Long paymentMethodId,
                                                          @RequestParam("amount") BigDecimal amount,
                                                          @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                          @RequestParam("status") String status,
                                                          @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(rentalId, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @DeleteMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> deleteRentalPayment(@PathVariable Long id) {
        service.deleteRentalPayment(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental payment"));
    }

    private RentalPaymentRequest rentalPaymentRequest(Long rentalId, Long paymentMethodId, BigDecimal amount,
                                                      LocalDateTime paymentDate, String status, MultipartFile proofImage) {
        RentalPaymentRequest request = new RentalPaymentRequest();
        request.setRentalId(rentalId);
        request.setPaymentMethodId(paymentMethodId);
        request.setAmount(amount);
        request.setPaymentDate(paymentDate);
        request.setStatus(status);
        request.setProofImageUpload(proofImage);
        return request;
    }
}
