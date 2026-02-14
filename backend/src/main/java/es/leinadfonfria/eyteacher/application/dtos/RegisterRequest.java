package es.leinadfonfria.eyteacher.application.dtos;

import es.leinadfonfria.eyteacher.domain.entities.Role;

/**
 * Data transfer object for user registration requests.
 * Captures all necessary information to create a new user account.
 *
 * @param email     The desired email address for the new account.
 * @param password  The plain-text password chosen by the user.
 * @param firstName The user's first name.
 * @param lastName  The user's last name.
 * @param isAdmin   Flag indicating if the user should be created with administrator privileges.
 */
public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        Boolean isAdmin
) {
}
