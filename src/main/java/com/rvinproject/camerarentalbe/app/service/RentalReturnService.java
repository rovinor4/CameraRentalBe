package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.ReturnRequest;
import com.rvinproject.camerarentalbe.app.enumModel.RentalStatus;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.model.Rental;
import com.rvinproject.camerarentalbe.app.model.RentalReturn;
import com.rvinproject.camerarentalbe.app.repository.RentalReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RentalReturnService {
    private final RentalReturnRepository rentalReturnRepository;
    private final RentalService rentalService;
    private final PaymentMethodService paymentMethodService;

    public Page<RentalReturn> returns(Specification<RentalReturn> specification, Pageable pageable) {
        return rentalReturnRepository.findAll(specification, pageable);
    }

    public RentalReturn rentalReturn(Long id) {
        return rentalReturnRepository.findById(id).orElseThrow(() -> notFound("return"));
    }

    @Transactional
    public RentalReturn saveReturn(Long id, ReturnRequest request, Admin admin) {
        RentalReturn rentalReturn = id == null ? new RentalReturn() : rentalReturn(id);
        Rental rental = rentalService.rental(request.getRentalId());
        if (id == null && rentalReturnRepository.existsByRentalId(rental.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental sudah memiliki return");
        }
        if (RentalStatus.cancelled.equals(rental.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental cancelled tidak bisa direturn");
        }
        rentalReturn.setRental(rental);
        rentalReturn.setAdmin(admin);
        rentalReturn.setReturnDate(request.getReturnDate());
        rentalReturn.setConditionNote(request.getConditionNote());
        rentalReturn.setHasPenalty(request.getHasPenalty());
        rentalReturn.setPenaltyPaymentMethod(request.getPenaltyPaymentMethodId() == null ? null : paymentMethodService.activePaymentMethod(request.getPenaltyPaymentMethodId()));
        rental.setActualReturnDate(request.getReturnDate());
        rental.setStatus(RentalStatus.returned);
        rentalService.restoreStock(rental);
        return rentalReturnRepository.save(rentalReturn);
    }

    public void deleteReturn(Long id) {
        rentalReturnRepository.delete(rentalReturn(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
