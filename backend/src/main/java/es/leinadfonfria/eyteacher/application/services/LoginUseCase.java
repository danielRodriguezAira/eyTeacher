package es.leinadfonfria.eyteacher.application.services;

import es.leinadfonfria.eyteacher.application.dtos.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.LoginRequest;

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
     * @return AuthUserResponse Containing the JWT token and user details.
     */
    AuthUserResponse login(LoginRequest request);
}
