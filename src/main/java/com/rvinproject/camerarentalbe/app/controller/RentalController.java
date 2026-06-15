package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.dto.request.RentalRequest;
import com.rvinproject.camerarentalbe.app.service.RentalService;
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
public class RentalController {
    private final RentalService service;
    private static final List<String> RENTAL_SEARCH_FIELDS = List.of("id", "customer.name", "admin.name", "rentalCode", "rentalDate", "plannedReturnDate", "actualReturnDate", "totalPrice", "status", "note", "createdAt", "updatedAt");

    @GetMapping("/api/rentals")
    public ResponseEntity<?> rentals(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return ResponseEntity.ok(JSONFormat.success(PageUtil.response(service.rentals(
                QueryUtil.specification(params, RENTAL_SEARCH_FIELDS, "rentalDate"),
                PageableUtil.from(page, size, params)
        )), "Berhasil mengambil rental"));
    }

    @GetMapping("/api/rentals/{id}")
    public ResponseEntity<?> rental(@PathVariable Long id) {
        return ResponseEntity.ok(JSONFormat.success(service.rental(id), "Berhasil mengambil rental"));
    }

    @GetMapping("/api/rental-not-returned")
    public ResponseEntity<?> rentalNotReturned() {
        return ResponseEntity.ok(JSONFormat.success(service.rentalNotReturned(), "Berhasil mengambil rental belum kembali"));
    }

    @PostMapping("/api/rentals")
    public ResponseEntity<?> createRental(@Valid @RequestBody RentalRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(JSONFormat.success(service.createRental(body, AuthUtil.admin(request)), "Berhasil membuat rental"));
    }

    @PutMapping("/api/rentals/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id, @Valid @RequestBody RentalRequest body) {
        return ResponseEntity.ok(JSONFormat.success(service.updateRental(id, body), "Berhasil mengubah rental"));
    }

    @DeleteMapping("/api/rentals/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        service.deleteRental(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental"));
    }

    @DeleteMapping("/api/rental-details/{id}")
    public ResponseEntity<?> deleteRentalDetail(@PathVariable Long id) {
        service.deleteRentalDetail(id);
        return ResponseEntity.ok(JSONFormat.success(null, "Berhasil menghapus rental detail"));
    }
}
