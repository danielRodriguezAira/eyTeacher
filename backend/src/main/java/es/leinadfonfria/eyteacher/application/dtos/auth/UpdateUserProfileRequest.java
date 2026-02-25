package es.leinadfonfria.eyteacher.application.dtos.auth;

/**
 * Data transfer object for updating an existing user.
 * Only allows changing email, firstName and lastName.
 */
public record UpdateUserProfileRequest(
        String id,
        String email,
        String firstName,
        String lastName
) {
}
