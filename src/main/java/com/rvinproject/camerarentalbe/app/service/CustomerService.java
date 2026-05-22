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
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Page<Customer> customers(Specification<Customer> specification, Pageable pageable) {
        return customerRepository.findAll(specification, pageable);
    }

    public Customer customer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> notFound("customer"));
    }

    public Customer saveCustomer(Long id, CustomerRequest request) {
        Customer customer = id == null ? new Customer() : customer(id);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setIdentityType(ValidationUtil.enumValue(request.getIdentityType(), IdentityType.class, "identityType"));
        customer.setIdentityNumber(request.getIdentityNumber());
        customer.setIdentityImage(request.getIdentityImage());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.delete(customer(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
