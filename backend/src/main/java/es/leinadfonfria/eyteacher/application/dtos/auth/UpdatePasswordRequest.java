package es.leinadfonfria.eyteacher.application.dtos.auth;

public record UpdatePasswordRequest(
        String id,
        String oldPassword,
        String newPassword
) {
}
