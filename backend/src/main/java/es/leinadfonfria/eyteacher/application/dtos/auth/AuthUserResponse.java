package es.leinadfonfria.eyteacher.application.dtos.auth;

import es.leinadfonfria.eyteacher.domain.entities.Role;

import java.time.Instant;

/**
 * Data transfer object for authentication responses.
 * Contains the JWT token and basic user information after a successful login or registration.
 *
 * @param token      The JWT access token.
 * @param isAdmin    Flag indicating if the user has administrator privileges.
 * @param email      The user's email address.
 * @param id         The user's unique identifier.
 * @param expiration The expiration timestamp of the token.
 * @param firstName  The user's first name.
 * @param lastName   The user's last name.
 * @param role       The role selected for the session.
 */
public record AuthUserResponse(
        String token,
        boolean isAdmin,
        String email,
        String id,
        Instant expiration,
        String firstName,
        String lastName,
        Role role
) {
}
