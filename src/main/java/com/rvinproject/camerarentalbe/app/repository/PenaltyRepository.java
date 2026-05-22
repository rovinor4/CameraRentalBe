package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long>, JpaSpecificationExecutor<Penalty> {
    List<Penalty> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
