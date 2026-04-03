package es.leinadfonfria.eyteacher.ai.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Intercepts authenticated API requests and enforces per-user rate limits.
 *
 * <p>Extracts the user ID from the {@link Jwt} principal already decoded by
 * Spring Security, then delegates to {@link RateLimitService} to check whether
 * the request is within the configured hourly and daily limits.</p>
 *
 * <p>Returns {@code 429 Too Many Requests} with a JSON error body when a limit
 * is exceeded.</p>
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    /**
     * Creates a {@code RateLimitInterceptor} with the required rate limit service.
     *
     * @param rateLimitService the service that manages per-user token buckets
     */
    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Checks rate limits before the request reaches the controller.
     *
     * <p>If the authenticated user has exceeded either the hourly or daily limit,
     * the response is completed with HTTP 429 and {@code false} is returned to
     * stop further processing. If no authenticated user is found, the request
     * passes through (authentication is handled separately by Spring Security).</p>
     *
     * @param request  the incoming HTTP request
     * @param response the HTTP response
     * @param handler  the handler that would process the request
     * @return {@code true} if the request is within rate limits and should proceed,
     *         {@code false} if the rate limit has been exceeded
     * @throws IOException if writing the error response fails
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return true;
        }

        String userId = jwt.getSubject();

        if (!rateLimitService.tryConsume(userId)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Try again later.\"}");
            return false;
        }

        return true;
    }
}
