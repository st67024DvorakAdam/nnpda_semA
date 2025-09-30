package cz.upce.fei.nnpda.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Když je token neplatný nebo expiroval, pošli 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: JWT token invalid or expired");
    }
}
