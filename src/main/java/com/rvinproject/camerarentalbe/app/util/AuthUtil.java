package com.rvinproject.camerarentalbe.app.util;

import com.rvinproject.camerarentalbe.app.enumModel.AdminRole;
import com.rvinproject.camerarentalbe.app.model.Admin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthUtil {
    private AuthUtil() {
    }

    public static Admin admin(HttpServletRequest request) {
        Object authAdmin = request.getAttribute("auth_admin");
        if (authAdmin instanceof Admin admin) {
            return admin;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token tidak valid");
    }

    public static void requireSuperAdmin(Admin admin) {
        if (!AdminRole.super_admin.equals(admin.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akses ditolak");
        }
    }
}
