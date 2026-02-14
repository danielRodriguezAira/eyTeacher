package es.leinadfonfria.eyteacher.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthorized access attempts.
 * This entry point is called when an unauthenticated user tries to access a protected resource.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences the authentication scheme.
     * Returns a 401 Unauthorized error to the client.
     *
     * @param request       The HTTP request.
     * @param response      The HTTP response.
     * @param authException The exception that was thrown.
     * @throws IOException If an input or output exception occurs.
     */
    @Override
    public void commence(jakarta.servlet.http.@NonNull HttpServletRequest request,
                         jakarta.servlet.http.HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
