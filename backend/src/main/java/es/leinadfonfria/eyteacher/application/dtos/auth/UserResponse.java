package es.leinadfonfria.eyteacher.application.dtos.auth;

public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName
) {}
