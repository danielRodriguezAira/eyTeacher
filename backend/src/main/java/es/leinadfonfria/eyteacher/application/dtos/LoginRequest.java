package es.leinadfonfria.eyteacher.application.dtos;

/**
 * Data transfer object for login requests.
 * Captures the credentials provided by the user during the login process.
 *
 * @param email    The user's email address.
 * @param password The user's plain-text password.
 * @param role     Optional role reference used by the frontend.
 */
public record LoginRequest(
        String email,
        String password,
        String role // frontend-only reference
) {
}
