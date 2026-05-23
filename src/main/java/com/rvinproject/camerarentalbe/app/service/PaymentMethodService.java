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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final FileStorageService fileStorageService;

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
        String oldImageUpload = paymentMethod.getImageUpload();
        paymentMethod.setName(request.getName());
        paymentMethod.setType(ValidationUtil.enumValue(request.getType(), PaymentMethodType.class, "type"));
        PaymentContentType contentType = ValidationUtil.enumValue(request.getContentType(), PaymentContentType.class, "content_type");
        paymentMethod.setContentType(contentType);
        if (PaymentContentType.text.equals(contentType)) {
            if (!StringUtils.hasText(request.getContentValue())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content_value wajib diisi jika content_type text");
            }
            paymentMethod.setContentValue(request.getContentValue());
            paymentMethod.setImageUpload(null);
        } else {
            String uploadedImage = fileStorageService.storeImage(request.getImageUpload(), "payment-methods");
            if (uploadedImage == null && id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image_upload wajib diisi jika content_type image");
            }
            if (uploadedImage == null && !StringUtils.hasText(paymentMethod.getImageUpload())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image_upload wajib diisi jika content_type image");
            }
            paymentMethod.setContentValue(null);
            if (uploadedImage != null) {
                paymentMethod.setImageUpload(uploadedImage);
            }
        }
        paymentMethod.setActive(request.getActive());
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        if (!StringUtils.hasText(savedPaymentMethod.getImageUpload()) || !savedPaymentMethod.getImageUpload().equals(oldImageUpload)) {
            fileStorageService.deleteStoredFile(oldImageUpload);
        }
        return savedPaymentMethod;
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethod(id);
        paymentMethodRepository.delete(paymentMethod);
        fileStorageService.deleteStoredFile(paymentMethod.getImageUpload());
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
