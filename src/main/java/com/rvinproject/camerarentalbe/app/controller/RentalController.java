package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
import com.rvinproject.camerarentalbe.app.service.RentalService;
import com.rvinproject.camerarentalbe.app.util.AuthUtil;
import com.rvinproject.camerarentalbe.app.util.PageUtil;
import com.rvinproject.camerarentalbe.app.util.QueryUtil;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RentalController {
    private final RentalService service;
    private static final List<String> RENTAL_SEARCH_FIELDS = List.of("id", "customer.name", "admin.name", "rentalCode", "rentalDate", "plannedReturnDate", "actualReturnDate", "totalPrice", "status", "note", "createdAt", "updatedAt");
    private static final List<String> PAYMENT_SEARCH_FIELDS = List.of("id", "rental.rentalCode", "paymentMethod.name", "paymentCode", "amount", "paymentDate", "status", "proofImage", "createdAt", "updatedAt");
    private static final List<String> RETURN_SEARCH_FIELDS = List.of("id", "rental.rentalCode", "admin.name", "returnDate", "conditionNote", "hasPenalty", "penaltyPaymentMethod.name", "createdAt", "updatedAt");
    private static final List<String> PENALTY_SEARCH_FIELDS = List.of("id", "rentalReturn.rental.rentalCode", "penaltyType", "description", "amount", "status", "paidAt", "createdAt", "updatedAt");
    private static final List<String> MAINTENANCE_SEARCH_FIELDS = List.of("id", "item.name", "admin.name", "title", "description", "maintenanceDate", "cost", "status", "createdAt", "updatedAt");

    @GetMapping("/api/rentals")
    public ResponseEntity<?> rentals(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.rentals(
                QueryUtil.specification(params, RENTAL_SEARCH_FIELDS, "rentalDate"),
                pageable(page, size, params)
        )), "Berhasil mengambil rental"));
    }

    @GetMapping("/api/rentals/{id}")
    public ResponseEntity<?> rental(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rental(id), "Berhasil mengambil rental"));
    }

    @PostMapping("/api/rentals")
    public ResponseEntity<?> createRental(@Valid @RequestBody ApiRequest.RentalRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.createRental(body, AuthUtil.admin(request)), "Berhasil membuat rental"));
    }

    @PutMapping("/api/rentals/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id, @Valid @RequestBody ApiRequest.RentalRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.updateRental(id, body), "Berhasil mengubah rental"));
    }

    @DeleteMapping("/api/rentals/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        service.deleteRental(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental"));
    }

    @GetMapping("/api/rental-payments")
    public ResponseEntity<?> rentalPayments(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.rentalPayments(
                QueryUtil.dateTimeSpecification(params, PAYMENT_SEARCH_FIELDS, "paymentDate"),
                pageable(page, size, params)
        )), "Berhasil mengambil rental payment"));
    }

    @GetMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> rentalPayment(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalPayment(id), "Berhasil mengambil rental payment"));
    }

    @PostMapping("/api/rental-payments")
    public ResponseEntity<?> createRentalPayment(@Valid @RequestBody ApiRequest.RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(null, body), "Berhasil membuat rental payment"));
    }

    @PutMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> updateRentalPayment(@PathVariable Long id, @Valid @RequestBody ApiRequest.RentalPaymentRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.saveRentalPayment(id, body), "Berhasil mengubah rental payment"));
    }

    @DeleteMapping("/api/rental-payments/{id}")
    public ResponseEntity<?> deleteRentalPayment(@PathVariable Long id) {
        service.deleteRentalPayment(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental payment"));
    }

    @GetMapping("/api/returns")
    public ResponseEntity<?> returns(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.returns(
                QueryUtil.specification(params, RETURN_SEARCH_FIELDS, "returnDate"),
                pageable(page, size, params)
        )), "Berhasil mengambil return"));
    }

    @GetMapping("/api/returns/{id}")
    public ResponseEntity<?> rentalReturn(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rentalReturn(id), "Berhasil mengambil return"));
    }

    @PostMapping("/api/returns")
    public ResponseEntity<?> createReturn(@Valid @RequestBody ApiRequest.ReturnRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.saveReturn(null, body, AuthUtil.admin(request)), "Berhasil membuat return"));
    }

    @PutMapping("/api/returns/{id}")
    public ResponseEntity<?> updateReturn(@PathVariable Long id, @Valid @RequestBody ApiRequest.ReturnRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.saveReturn(id, body, AuthUtil.admin(request)), "Berhasil mengubah return"));
    }

    @DeleteMapping("/api/returns/{id}")
    public ResponseEntity<?> deleteReturn(@PathVariable Long id) {
        service.deleteReturn(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus return"));
    }

    @GetMapping("/api/denda")
    public ResponseEntity<?> penalties(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.penalties(
                QueryUtil.dateTimeSpecification(params, PENALTY_SEARCH_FIELDS, "createdAt"),
                pageable(page, size, params)
        )), "Berhasil mengambil denda"));
    }

    @GetMapping("/api/denda/{id}")
    public ResponseEntity<?> penalty(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.penalty(id), "Berhasil mengambil denda"));
    }

    @PostMapping("/api/denda")
    public ResponseEntity<?> createPenalty(@Valid @RequestBody ApiRequest.PenaltyRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.savePenalty(null, body), "Berhasil membuat denda"));
    }

    @PutMapping("/api/denda/{id}")
    public ResponseEntity<?> updatePenalty(@PathVariable Long id, @Valid @RequestBody ApiRequest.PenaltyRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.savePenalty(id, body), "Berhasil mengubah denda"));
    }

    @DeleteMapping("/api/denda/{id}")
    public ResponseEntity<?> deletePenalty(@PathVariable Long id) {
        service.deletePenalty(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus denda"));
    }

    @GetMapping("/api/maintenance")
    public ResponseEntity<?> maintenances(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.maintenances(
                QueryUtil.specification(params, MAINTENANCE_SEARCH_FIELDS, "maintenanceDate"),
                pageable(page, size, params)
        )), "Berhasil mengambil maintenance"));
    }

    @GetMapping("/api/maintenance/{id}")
    public ResponseEntity<?> maintenance(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.maintenance(id), "Berhasil mengambil maintenance"));
    }

    @PostMapping("/api/maintenance")
    public ResponseEntity<?> createMaintenance(@Valid @RequestBody ApiRequest.MaintenanceRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveMaintenance(null, body, AuthUtil.admin(request)), "Berhasil membuat maintenance"));
    }

    @PutMapping("/api/maintenance/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Long id, @Valid @RequestBody ApiRequest.MaintenanceRequest body, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        return ResponseEntity.ok(JSONFormat.success(service.saveMaintenance(id, body, AuthUtil.admin(request)), "Berhasil mengubah maintenance"));
    }

    @DeleteMapping("/api/maintenance/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long id, HttpServletRequest request) {
        AuthUtil.requireSuperAdmin(AuthUtil.admin(request));
        service.deleteMaintenance(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus maintenance"));
    }

    private Pageable pageable(int page, int size, Map<String, String[]> params) {
        Sort.Direction direction = "asc".equalsIgnoreCase(QueryUtil.direction(params)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 100)), Sort.by(direction, QueryUtil.sortField(params)));
    }
}
