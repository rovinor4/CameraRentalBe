package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.list.RoleUser;
import com.rvinproject.camerarentalbe.app.model.Token;
import com.rvinproject.camerarentalbe.app.model.User;
import com.rvinproject.camerarentalbe.app.repository.TokenRepository;
import com.rvinproject.camerarentalbe.app.repository.UserRepository;
import com.rvinproject.camerarentalbe.app.request.AuthRequest.LoginRequest;
import com.rvinproject.camerarentalbe.app.request.AuthRequest.RegisterRequest;
import com.rvinproject.camerarentalbe.helper.JSONFormat;
import com.rvinproject.camerarentalbe.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            errors.put("username", "Username Kosong");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            errors.put("password", "Password Kosong");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(JSONFormat.error(errors, null));
        }

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(JSONFormat.error(null, "Username / Password salah"));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(JSONFormat.error(null, "Username / Password salah"));
        }

        try {
            Token token = new Token();
            token.setUser(user);
            token.setToken(StringHelper.RandomString(100));
            token.setExpiresAt(LocalDateTime.now().plusDays(7));

            Token savedToken = tokenRepository.save(token);

            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("username", user.getUsername());
            userData.put("role", user.getRole());
            userData.put("createdAt", user.getCreatedAt());
            userData.put("updatedAt", user.getUpdatedAt());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token", savedToken.getToken());
            response.put("data", userData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(JSONFormat.error(null, "Gagal membuat token"));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.getName() == null || request.getName().isBlank()) {
            errors.put("name", "Nama Kosong");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            errors.put("username", "Username Kosong");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            errors.put("password", "Password Kosong");
        }

        if (request.getPasswordConfirmation() == null || request.getPasswordConfirmation().isBlank()) {
            errors.put("password_confirmation", "Konfirmasi Password Kosong");
        }

        if (request.getPassword() != null
                && request.getPasswordConfirmation() != null
                && !request.getPassword().equals(request.getPasswordConfirmation())) {
            errors.put("password_confirmation", "Konfirmasi Password Tidak Sama");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(JSONFormat.error(errors, null));
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            errors.put("username", "Username udah ditemukan");
            return ResponseEntity.badRequest().body(JSONFormat.error(errors, null));
        }

        try {
            User user = new User();
            user.setName(request.getName());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(RoleUser.CUSTOMER);

            userRepository.save(user);

            return ResponseEntity.ok(JSONFormat.success(null, "Berhasil terdaftar"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(JSONFormat.error(null, e.getMessage()));
        }
    }
}