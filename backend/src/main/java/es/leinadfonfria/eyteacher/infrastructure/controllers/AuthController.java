package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.dtos.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.LoginRequest;
import es.leinadfonfria.eyteacher.application.dtos.RegisterRequest;
import es.leinadfonfria.eyteacher.application.services.LoginUseCase;
import es.leinadfonfria.eyteacher.application.services.RegisterUseCase;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for authentication operations.
 * Provides endpoints for user login and registration.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    /**
     * Handles user login requests.
     * Authenticates the user and returns a JWT token if successful.
     *
     * @param request The login credentials.
     * @return ResponseEntity<AuthUserResponse> HTTP 200 with token and user info, or HTTP 401 if unauthorized.
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthUserResponse> login(@RequestBody LoginRequest request) {
        log.info("Logging in user with email: {}", request);
        return ResponseEntity.ok(loginUseCase.login(request));
    }

    /**
     * Handles user registration requests.
     * Creates a new user account in the system.
     *
     * @param request The registration details.
     * @return ResponseEntity<Map<String, String>> HTTP 200 with the new user's ID.
     */
    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Creates a new user account")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        log.info("Registering user: {}", request);
        UserId userId = registerUseCase.register(request);
        log.info("Registered user with ID: {}", userId);
        return ResponseEntity.ok(Map.of("id", userId.value().toString()));
    }
}
