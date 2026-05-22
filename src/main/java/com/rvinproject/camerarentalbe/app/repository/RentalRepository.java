package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    List<Rental> findByRentalDateBetween(LocalDate startDate, LocalDate endDate);
}
