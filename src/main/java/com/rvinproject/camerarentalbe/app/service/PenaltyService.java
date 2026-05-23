package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.PenaltyRequest;
import com.rvinproject.camerarentalbe.app.enumModel.PenaltyStatus;
import com.rvinproject.camerarentalbe.app.enumModel.PenaltyType;
import com.rvinproject.camerarentalbe.app.model.Penalty;
import com.rvinproject.camerarentalbe.app.repository.PenaltyRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    private final PenaltyRepository penaltyRepository;
    private final RentalReturnService rentalReturnService;

    public Page<Penalty> penalties(Specification<Penalty> specification, Pageable pageable) {
        return penaltyRepository.findAll(specification, pageable);
    }

    public Penalty penalty(Long id) {
        return penaltyRepository.findById(id).orElseThrow(() -> notFound("denda"));
    }

    public Penalty savePenalty(Long id, PenaltyRequest request) {
        Penalty penalty = id == null ? new Penalty() : penalty(id);
        PenaltyStatus status = ValidationUtil.enumValue(request.getStatus(), PenaltyStatus.class, "status");
        penalty.setRentalReturn(rentalReturnService.rentalReturn(request.getReturnId()));
        penalty.setPenaltyType(ValidationUtil.enumValue(request.getPenaltyType(), PenaltyType.class, "penalty_type"));
        penalty.setDescription(request.getDescription());
        penalty.setAmount(request.getAmount());
        penalty.setStatus(status);
        penalty.setPaidAt(PenaltyStatus.paid.equals(status) && request.getPaidAt() == null ? java.time.LocalDateTime.now() : request.getPaidAt());
        return penaltyRepository.save(penalty);
    }

    public void deletePenalty(Long id) {
        penaltyRepository.delete(penalty(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
