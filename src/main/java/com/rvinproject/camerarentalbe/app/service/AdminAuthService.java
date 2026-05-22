package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.AdminRequest;
import com.rvinproject.camerarentalbe.app.dto.request.LoginRequest;
import com.rvinproject.camerarentalbe.app.enumModel.AdminRole;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.model.AdminSession;
import com.rvinproject.camerarentalbe.app.repository.AdminRepository;
import com.rvinproject.camerarentalbe.app.repository.AdminSessionRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import com.rvinproject.camerarentalbe.helper.StringHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {
    private final AdminRepository adminRepository;
    private final AdminSessionRepository adminSessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Map<String, Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email / Password salah"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email / Password salah");
        }

        AdminSession session = new AdminSession();
        session.setAdmin(admin);
        session.setToken(StringHelper.RandomString(100));
        session.setIpAddress(httpRequest.getRemoteAddr());
        session.setUserAgent(httpRequest.getHeader("User-Agent"));
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        adminSessionRepository.save(session);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", session.getToken());
        response.put("data", adminResponse(admin));
        return response;
    }

    @Transactional
    public void logout(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            adminSessionRepository.deleteByToken(authorization.substring(7));
        }
    }

    public Page<Map<String, Object>> allAdmins(Specification<Admin> specification, Pageable pageable) {
        return adminRepository.findAll(specification, pageable).map(this::adminResponse);
    }

    public Map<String, Object> findAdmin(Long id) {
        return adminResponse(admin(id));
    }

    public Map<String, Object> createAdmin(AdminRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password wajib diisi");
        }
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email sudah digunakan");
        }
        Admin admin = new Admin();
        apply(admin, request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        return adminResponse(adminRepository.save(admin));
    }

    public Map<String, Object> updateAdmin(Long id, AdminRequest request) {
        Admin admin = admin(id);
        if (!admin.getEmail().equals(request.getEmail()) && adminRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email sudah digunakan");
        }
        apply(admin, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return adminResponse(adminRepository.save(admin));
    }

    public void deleteAdmin(Long id) {
        adminRepository.delete(admin(id));
    }

    private void apply(Admin admin, AdminRequest request) {
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setRole(ValidationUtil.enumValue(request.getRole(), AdminRole.class, "role"));
    }

    public Admin admin(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "admin tidak ditemukan"));
    }

    private Map<String, Object> adminResponse(Admin admin) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", admin.getId());
        data.put("name", admin.getName());
        data.put("email", admin.getEmail());
        data.put("role", admin.getRole());
        data.put("createdAt", admin.getCreatedAt());
        data.put("updatedAt", admin.getUpdatedAt());
        return data;
    }
}
