package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.RentalPaymentRequest;
import com.rvinproject.camerarentalbe.app.enumModel.PaymentStatus;
import com.rvinproject.camerarentalbe.app.model.RentalPayment;
import com.rvinproject.camerarentalbe.app.repository.RentalPaymentRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import com.rvinproject.camerarentalbe.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RentalPaymentService {
    private final RentalPaymentRepository rentalPaymentRepository;
    private final RentalService rentalService;
    private final PenaltyService penaltyService;
    private final PaymentMethodService paymentMethodService;
    private final FileStorageService fileStorageService;

    public Page<RentalPayment> rentalPayments(Specification<RentalPayment> specification, Pageable pageable) {
        return rentalPaymentRepository.findAll(specification, pageable);
    }

    public RentalPayment rentalPayment(Long id) {
        return rentalPaymentRepository.findById(id).orElseThrow(() -> notFound("rental payment"));
    }

    public java.util.List<RentalPayment> rentalPaymentsByRental(Long rentalId) {
        rentalService.rental(rentalId);
        return rentalPaymentRepository.findByRentalId(rentalId);
    }

    public java.util.List<RentalPayment> rentalPaymentsByPenalty(Long penaltyId) {
        penaltyService.penalty(penaltyId);
        return rentalPaymentRepository.findByPenaltyId(penaltyId);
    }

    public RentalPayment saveRentalPayment(Long id, RentalPaymentRequest request) {
        RentalPayment payment = id == null ? new RentalPayment() : rentalPayment(id);
        String oldProofImage = payment.getProofImage();
        validateTarget(request.getRentalId(), request.getPenaltyId());
        payment.setRental(request.getRentalId() == null ? null : rentalService.rental(request.getRentalId()));
        payment.setPenalty(request.getPenaltyId() == null ? null : penaltyService.penalty(request.getPenaltyId()));
        payment.setPaymentMethod(paymentMethodService.activePaymentMethod(request.getPaymentMethodId()));
        if (payment.getPaymentCode() == null) {
            payment.setPaymentCode("PAY-" + StringHelper.RandomString(12));
        }
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(ValidationUtil.enumValue(request.getStatus(), PaymentStatus.class, "status"));
        String uploadedProofImage = fileStorageService.storeImage(request.getProofImageUpload(), "rental-payments");
        if (uploadedProofImage == null && StringUtils.hasText(request.getProofImage())) {
            payment.setProofImage(request.getProofImage());
        }
        if (uploadedProofImage == null && id == null && !StringUtils.hasText(payment.getProofImage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "proof_image_upload wajib diisi");
        }
        if (uploadedProofImage != null) {
            payment.setProofImage(uploadedProofImage);
        } else if (!StringUtils.hasText(payment.getProofImage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "proof_image_upload wajib diisi");
        }
        RentalPayment savedPayment = rentalPaymentRepository.save(payment);
        if (uploadedProofImage != null) {
            fileStorageService.deleteStoredFile(oldProofImage);
        }
        return savedPayment;
    }

    public void deleteRentalPayment(Long id) {
        RentalPayment payment = rentalPayment(id);
        rentalPaymentRepository.delete(payment);
        fileStorageService.deleteStoredFile(payment.getProofImage());
    }

    private void validateTarget(Long rentalId, Long penaltyId) {
        if (rentalId == null && penaltyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rental_id atau penalty_id wajib diisi");
        }
        if (rentalId != null && penaltyId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payment detail hanya boleh punya satu target rental_id atau penalty_id");
        }
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
