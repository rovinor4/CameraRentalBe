package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.CustomerRequest;
import com.rvinproject.camerarentalbe.app.enumModel.IdentityType;
import com.rvinproject.camerarentalbe.app.model.Customer;
import com.rvinproject.camerarentalbe.app.repository.CustomerRepository;
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
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    public Page<Customer> customers(Specification<Customer> specification, Pageable pageable) {
        return customerRepository.findAll(specification, pageable);
    }

    public Customer customer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> notFound("customer"));
    }

    public Customer saveCustomer(Long id, CustomerRequest request) {
        Customer customer = id == null ? new Customer() : customer(id);
        String oldIdentityImage = customer.getIdentityImage();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setIdentityType(ValidationUtil.enumValue(request.getIdentityType(), IdentityType.class, "identity_type"));
        customer.setIdentityNumber(request.getIdentityNumber());
        String uploadedIdentityImage = fileStorageService.storeImage(request.getIdentityImageUpload(), "customers");
        if (uploadedIdentityImage == null && id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "identity_image_upload wajib diisi");
        }
        if (uploadedIdentityImage != null) {
            customer.setIdentityImage(uploadedIdentityImage);
        } else if (!StringUtils.hasText(customer.getIdentityImage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "identity_image_upload wajib diisi");
        }
        Customer savedCustomer = customerRepository.save(customer);
        if (uploadedIdentityImage != null) {
            fileStorageService.deleteStoredFile(oldIdentityImage);
        }
        return savedCustomer;
    }

    public void deleteCustomer(Long id) {
        Customer customer = customer(id);
        customerRepository.delete(customer);
        fileStorageService.deleteStoredFile(customer.getIdentityImage());
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
