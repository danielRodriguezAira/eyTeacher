package es.leinadfonfria.eyteacher.application.dtos.auth;

import es.leinadfonfria.eyteacher.domain.entities.Role;

/**
 * Data transfer object for login requests.
 * Captures the credentials provided by the user during the login process.
 *
 * @param email    The user's email address.
 * @param password The user's plain-text password.
 * @param role     The role of the user (TEACHER or STUDENT).
 */
public record LoginRequest(
        String email,
        String password,
        Role role
) {
}
