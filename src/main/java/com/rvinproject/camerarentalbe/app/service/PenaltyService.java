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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public List<Penalty> penaltiesByReturn(Long returnId) {
        rentalReturnService.rentalReturn(returnId);
        return penaltyRepository.findByRentalReturnId(returnId);
    }

    public Penalty penaltyByReturn(Long returnId, Long penaltyId) {
        rentalReturnService.rentalReturn(returnId);
        return penaltyRepository.findByIdAndRentalReturnId(penaltyId, returnId).orElseThrow(() -> notFound("denda"));
    }

    public Penalty savePenalty(Long id, PenaltyRequest request) {
        if (request.getReturnId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "return_id wajib diisi");
        }
        return savePenalty(id, request.getReturnId(), request);
    }

    @Transactional
    public Penalty savePenalty(Long id, Long returnId, PenaltyRequest request) {
        Penalty penalty = id == null ? new Penalty() : penalty(id);
        Long previousReturnId = penalty.getRentalReturn() == null ? null : penalty.getRentalReturn().getId();
        PenaltyStatus status = ValidationUtil.enumValue(request.getStatus(), PenaltyStatus.class, "status");
        penalty.setRentalReturn(rentalReturnService.rentalReturn(returnId));
        penalty.setPenaltyType(ValidationUtil.enumValue(request.getPenaltyType(), PenaltyType.class, "penalty_type"));
        penalty.setDescription(request.getDescription());
        penalty.setAmount(request.getAmount());
        penalty.setStatus(status);
        penalty.setPaidAt(PenaltyStatus.paid.equals(status) && request.getPaidAt() == null ? java.time.LocalDateTime.now() : request.getPaidAt());
        Penalty savedPenalty = penaltyRepository.save(penalty);
        rentalReturnService.setHasPenalty(returnId, true);
        if (previousReturnId != null && !previousReturnId.equals(returnId) && penaltyRepository.countByRentalReturnId(previousReturnId) == 0) {
            rentalReturnService.setHasPenalty(previousReturnId, false);
        }
        return savedPenalty;
    }

    public Penalty updatePenaltyByReturn(Long returnId, Long penaltyId, PenaltyRequest request) {
        penaltyByReturn(returnId, penaltyId);
        return savePenalty(penaltyId, returnId, request);
    }

    @Transactional
    public void deletePenalty(Long id) {
        Penalty penalty = penalty(id);
        Long returnId = penalty.getRentalReturn().getId();
        penaltyRepository.delete(penalty);
        updateReturnPenaltyFlagAfterDelete(returnId);
    }

    @Transactional
    public void deletePenaltyByReturn(Long returnId, Long penaltyId) {
        penaltyRepository.delete(penaltyByReturn(returnId, penaltyId));
        updateReturnPenaltyFlagAfterDelete(returnId);
    }

    private void updateReturnPenaltyFlagAfterDelete(Long returnId) {
        penaltyRepository.flush();
        if (penaltyRepository.countByRentalReturnId(returnId) == 0) {
            rentalReturnService.setHasPenalty(returnId, false);
        }
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
