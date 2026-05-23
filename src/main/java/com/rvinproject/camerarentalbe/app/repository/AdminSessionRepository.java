package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminSessionRepository extends JpaRepository<AdminSession, Long> {
    Optional<AdminSession> findByToken(String token);

    @Query("select s from AdminSession s join fetch s.admin where s.token = :token")
    Optional<AdminSession> findByTokenWithAdmin(@Param("token") String token);

    void deleteByToken(String token);
}
