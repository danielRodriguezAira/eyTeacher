package es.leinadfonfria.eyteacher.application.dtos.notification;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.domain.entities.NotificationEntityType;

import java.time.LocalDateTime;

/**
 * Response DTO for a notification.
 *
 * <p>The frontend constructs the navigation link using {@code entityType} and {@code entityId},
 * keeping frontend routing decoupled from the backend.</p>
 */
public record NotificationResponse(
        Long id,
        UserResponse owner,
        String message,
        NotificationEntityType entityType,
        Long entityId,
        boolean read,
        LocalDateTime createdAt
) {}
