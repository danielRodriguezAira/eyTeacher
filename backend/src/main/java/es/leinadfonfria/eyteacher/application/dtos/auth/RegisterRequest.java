package es.leinadfonfria.eyteacher.application.dtos.auth;

/**
 * Data transfer object for user registration requests.
 * Captures all necessary information to create a new user account.
 *
 * @param email     The desired email address for the new account.
 * @param password  The plain-text password chosen by the user.
 * @param firstName The user's first firstName.
 * @param lastName  The user's last firstName.
 */
public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
