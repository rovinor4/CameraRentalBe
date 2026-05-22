package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    boolean existsByIdentityNumber(String identityNumber);
}
