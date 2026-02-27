package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.dtos.auth.LoginRequest;
import es.leinadfonfria.eyteacher.application.dtos.auth.RegisterRequest;
import es.leinadfonfria.eyteacher.application.dtos.auth.UpdatePasswordRequest;
import es.leinadfonfria.eyteacher.application.dtos.auth.UpdateUserProfileRequest;
import es.leinadfonfria.eyteacher.application.services.auth.LoginUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.RegisterUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.UpdatePasswordUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.UpdateUserProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Provides endpoints for user login and registration.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, registration and update")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    /**
     * Handles user login requests.
     * Authenticates the user and returns a JWT token if successful.
     *
     * @param request The login credentials.
     * @return ResponseEntity<AuthUserResponse> HTTP 200 with user.
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Logging in user with email: {}", request);
        return loginUseCase.login(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error)
                );
    }

    /**
     * Handles user registration requests.
     * Creates a new user account in the system.
     *
     * @param request The registration details.
     * @return ResponseEntity<?> HTTP 200 with the result: OK or BAD_REQUEST with an error code.
     */
    @PutMapping("/register")
    @Operation(summary = "Register user", description = "Creates a new user account")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Registering user: {}", request);
        return registerUseCase.register(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Handles user update requests.
     * Allows changing only email, firstName and lastName.
     *
     * @param request The update details.
     * @return ResponseEntity<?> HTTP 200 with the result: OK or BAD_REQUEST with an error code.
     */
    @PostMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates user's email, firstName and lastName")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserProfileRequest request) {
        log.info("Updating user profile: {}", request);
        return updateUserProfileUseCase.update(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Handles update password requests.
     * Verifies the current password and updates to a new password.
     *
     * @param request The update password details.
     * @return ResponseEntity<?> HTTP 200 with the result: OK or BAD_REQUEST with an error code.
     */
    @PostMapping("/update-password")
    @Operation(summary = "Update password", description = "Update user password after verifying current one")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        log.info("Updating password for id: {}", request.id());
        return updatePasswordUseCase.updatePassword(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
