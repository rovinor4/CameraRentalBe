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
    private static final List<String> SEARCH_FIELDS = List.of("id", "rental.rentalCode", "penalty.id", "paymentMethod.name", "paymentCode", "amount", "paymentDate", "status", "proofImage", "createdAt", "updatedAt");
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

    @GetMapping("/api/payment-details")
    public ResponseEntity<?> paymentDetails(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        return rentalPayments(page, size, request);
    }

    @GetMapping("/api/payment-details/{id}")
    public ResponseEntity<?> paymentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalPayment(id), "Berhasil mengambil payment detail"));
    }

    @GetMapping("/api/payment-details/rental/{rentalId}")
    public ResponseEntity<?> paymentDetailsByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalPaymentsByRental(rentalId), "Berhasil mengambil payment detail rental"));
    }

    @GetMapping("/api/payment-details/penalty/{penaltyId}")
    public ResponseEntity<?> paymentDetailsByPenalty(@PathVariable Long penaltyId) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalPaymentsByPenalty(penaltyId), "Berhasil mengambil payment detail denda"));
    }

    @PostMapping(value = "/api/rental-payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRentalPayment(@Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PostMapping(value = "/api/rental-payments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRentalPaymentMultipart(@RequestParam(value = "rental_id", required = false) Long rentalId,
                                                          @RequestParam(value = "penalty_id", required = false) Long penaltyId,
                                                          @RequestParam("payment_method_id") Long paymentMethodId,
                                                          @RequestParam("amount") BigDecimal amount,
                                                          @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                          @RequestParam("status") String status,
                                                          @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(rentalId, penaltyId, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PostMapping(value = "/api/payment-details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaymentDetail(@Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat payment detail"));
    }

    @PostMapping(value = "/api/payment-details/rental/{rentalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaymentDetailByRental(@PathVariable Long rentalId, @Valid @RequestBody RentalPaymentRequest body) {
        body.setRentalId(rentalId);
        body.setPenaltyId(null);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat payment detail rental"));
    }

    @PostMapping(value = "/api/payment-details/rental/{rentalId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaymentDetailByRentalMultipart(@PathVariable Long rentalId,
                                                                  @RequestParam("payment_method_id") Long paymentMethodId,
                                                                  @RequestParam("amount") BigDecimal amount,
                                                                  @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                                  @RequestParam("status") String status,
                                                                  @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(rentalId, null, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat payment detail rental"));
    }

    @PostMapping(value = "/api/payment-details/penalty/{penaltyId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaymentDetailByPenalty(@PathVariable Long penaltyId, @Valid @RequestBody RentalPaymentRequest body) {
        body.setRentalId(null);
        body.setPenaltyId(penaltyId);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat payment detail denda"));
    }

    @PostMapping(value = "/api/payment-details/penalty/{penaltyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaymentDetailByPenaltyMultipart(@PathVariable Long penaltyId,
                                                                   @RequestParam("payment_method_id") Long paymentMethodId,
                                                                   @RequestParam("amount") BigDecimal amount,
                                                                   @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                                   @RequestParam("status") String status,
                                                                   @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(null, penaltyId, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat payment detail denda"));
    }

    @PutMapping(value = "/api/rental-payments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRentalPayment(@PathVariable Long id, @Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @PutMapping(value = "/api/rental-payments/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRentalPaymentMultipart(@PathVariable Long id,
                                                          @RequestParam(value = "rental_id", required = false) Long rentalId,
                                                          @RequestParam(value = "penalty_id", required = false) Long penaltyId,
                                                          @RequestParam("payment_method_id") Long paymentMethodId,
                                                          @RequestParam("amount") BigDecimal amount,
                                                          @RequestParam(value = "payment_date", required = false) LocalDateTime paymentDate,
                                                          @RequestParam("status") String status,
                                                          @RequestParam(value = "proof_image", required = false) MultipartFile proofImage) {
        RentalPaymentRequest body = rentalPaymentRequest(rentalId, penaltyId, paymentMethodId, amount, paymentDate, status, proofImage);
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @PutMapping(value = "/api/payment-details/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePaymentDetail(@PathVariable Long id, @Valid @RequestBody RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah payment detail"));
    }

    @DeleteMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> deleteRentalPayment(@PathVariable Long id) {
        service.deleteRentalPayment(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental payment"));
    }

    @DeleteMapping("/api/payment-details/{id}")
    public ResponseEntity<?> deletePaymentDetail(@PathVariable Long id) {
        service.deleteRentalPayment(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus payment detail"));
    }

    private RentalPaymentRequest rentalPaymentRequest(Long rentalId, Long penaltyId, Long paymentMethodId, BigDecimal amount,
                                                      LocalDateTime paymentDate, String status, MultipartFile proofImage) {
        RentalPaymentRequest request = new RentalPaymentRequest();
        request.setRentalId(rentalId);
        request.setPenaltyId(penaltyId);
        request.setPaymentMethodId(paymentMethodId);
        request.setAmount(amount);
        request.setPaymentDate(paymentDate);
        request.setStatus(status);
        request.setProofImageUpload(proofImage);
        return request;
    }
}
