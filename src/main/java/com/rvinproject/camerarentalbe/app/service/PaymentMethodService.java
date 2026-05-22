package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.PaymentMethodRequest;
import com.rvinproject.camerarentalbe.app.enumModel.PaymentContentType;
import com.rvinproject.camerarentalbe.app.enumModel.PaymentMethodType;
import com.rvinproject.camerarentalbe.app.model.PaymentMethod;
import com.rvinproject.camerarentalbe.app.repository.PaymentMethodRepository;
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
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    public Page<PaymentMethod> paymentMethods(boolean activeOnly, Specification<PaymentMethod> specification, Pageable pageable) {
        if (activeOnly) {
            Specification<PaymentMethod> activeSpec = (root, query, builder) -> builder.isTrue(root.get("active"));
            return paymentMethodRepository.findAll(specification.and(activeSpec), pageable);
        }
        return paymentMethodRepository.findAll(specification, pageable);
    }

    public PaymentMethod paymentMethod(Long id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> notFound("payment method"));
    }

    public PaymentMethod activePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethod(id);
        if (!Boolean.TRUE.equals(paymentMethod.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payment method tidak aktif");
        }
        return paymentMethod;
    }

    public PaymentMethod savePaymentMethod(Long id, PaymentMethodRequest request) {
        PaymentMethod paymentMethod = id == null ? new PaymentMethod() : paymentMethod(id);
        paymentMethod.setName(request.getName());
        paymentMethod.setType(ValidationUtil.enumValue(request.getType(), PaymentMethodType.class, "type"));
        paymentMethod.setContentType(ValidationUtil.enumValue(request.getContentType(), PaymentContentType.class, "contentType"));
        paymentMethod.setContentValue(request.getContentValue());
        paymentMethod.setActive(request.getActive());
        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.delete(paymentMethod(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
