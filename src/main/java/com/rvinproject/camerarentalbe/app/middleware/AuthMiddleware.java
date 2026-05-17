package com.rvinproject.camerarentalbe.app.middleware;

import com.rvinproject.camerarentalbe.app.model.Token;
import com.rvinproject.camerarentalbe.app.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthMiddleware extends OncePerRequestFilter {
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            unauthorized(response);
            return;
        }

        String tokenValue = authorization.substring(7);
        Optional<Token> tokenOptional = tokenRepository.findByToken(tokenValue);

        if (tokenOptional.isEmpty()) {
            unauthorized(response);
            return;
        }

        Token token = tokenOptional.get();

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            unauthorized(response);
            return;
        }

        request.setAttribute("auth_user", token.getUser());
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register");
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "data": null,
                  "errors": null,
                  "message": "Token tidak valid"
                }
                """);
    }
}