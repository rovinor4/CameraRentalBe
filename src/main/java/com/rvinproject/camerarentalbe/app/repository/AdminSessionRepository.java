package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminSessionRepository extends JpaRepository<AdminSession, Long> {
    Optional<AdminSession> findByToken(String token);
    void deleteByToken(String token);
}
