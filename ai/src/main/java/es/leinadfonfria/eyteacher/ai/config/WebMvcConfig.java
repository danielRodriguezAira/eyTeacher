package es.leinadfonfria.eyteacher.ai.config;

import es.leinadfonfria.eyteacher.ai.ratelimit.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring MVC interceptors for the AI service.
 *
 * <p>Registers the {@link RateLimitInterceptor} on all {@code /api/v1/**} paths
 * so that every AI endpoint is subject to per-user rate limiting.</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    /**
     * Creates a {@code WebMvcConfig} with the required rate limit interceptor.
     *
     * @param rateLimitInterceptor the interceptor that enforces per-user rate limits
     */
    public WebMvcConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    /**
     * Registers the rate limit interceptor for all API endpoints.
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/v1/**");
    }
}
