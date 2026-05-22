package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.RentalReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface RentalReturnRepository extends JpaRepository<RentalReturn, Long>, JpaSpecificationExecutor<RentalReturn> {
    boolean existsByRentalId(Long rentalId);
    List<RentalReturn> findByReturnDateBetween(LocalDate startDate, LocalDate endDate);
}
