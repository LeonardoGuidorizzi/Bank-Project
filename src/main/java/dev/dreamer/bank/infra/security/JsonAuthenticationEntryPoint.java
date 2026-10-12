package dev.dreamer.bank.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Returns a JSON 401 for protected routes accessed without a valid token
 * (missing, malformed, tampered with or expired).
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public static final String MESSAGE = "Token de acesso ausente, inválido ou expirado";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("""
                {"status":401,"error":"Unauthorized","message":"%s","timestamp":"%s"}"""
                .formatted(MESSAGE, Instant.now()));
    }
}
