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
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RentalPaymentService {
    private final RentalPaymentRepository rentalPaymentRepository;
    private final RentalService rentalService;
    private final PaymentMethodService paymentMethodService;

    public Page<RentalPayment> rentalPayments(Specification<RentalPayment> specification, Pageable pageable) {
        return rentalPaymentRepository.findAll(specification, pageable);
    }

    public RentalPayment rentalPayment(Long id) {
        return rentalPaymentRepository.findById(id).orElseThrow(() -> notFound("rental payment"));
    }

    public RentalPayment saveRentalPayment(Long id, RentalPaymentRequest request) {
        RentalPayment payment = id == null ? new RentalPayment() : rentalPayment(id);
        payment.setRental(rentalService.rental(request.getRentalId()));
        payment.setPaymentMethod(paymentMethodService.activePaymentMethod(request.getPaymentMethodId()));
        if (payment.getPaymentCode() == null) {
            payment.setPaymentCode("PAY-" + StringHelper.RandomString(12));
        }
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(ValidationUtil.enumValue(request.getStatus(), PaymentStatus.class, "status"));
        payment.setProofImage(request.getProofImage());
        return rentalPaymentRepository.save(payment);
    }

    public void deleteRentalPayment(Long id) {
        rentalPaymentRepository.delete(rentalPayment(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
