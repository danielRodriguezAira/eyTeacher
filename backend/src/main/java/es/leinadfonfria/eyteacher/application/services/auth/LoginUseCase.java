package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.auth.LoginRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

/**
 * Interface for the user login use case.
 * Defines the contract for authenticating users and providing session information.
 */
public interface LoginUseCase {
    /**
     * Authenticates a user based on the provided credentials.
     * Validates the email and password, and generates a JWT token upon success.
     *
     * @param request The login credentials (email and password).
     * @return Result<AuthUserResponse, String> Containing the JWT token and user details or error message.
     */
    Result<AuthUserResponse, Integer> login(LoginRequest request);
}
