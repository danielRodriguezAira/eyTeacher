package es.leinadfonfria.eyteacher.application.dtos.notification;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;

public record NotificationResponse(
        Long id,
        UserResponse owner,
        String message,
        String goTo,
        boolean read
) {}
