package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.domain.entities.NotificationEntityType;

import java.util.UUID;

/**
 * Request DTO for creating a new notification.
 *
 * @param ownerId    The UUID of the user who will receive the notification.
 * @param message    The human-readable notification text.
 * @param entityType The type of entity this notification refers to.
 * @param entityId   The ID of the referenced entity.
 */
public record AddNotificationRequest(
        UUID ownerId,
        String message,
        NotificationEntityType entityType,
        Long entityId
) {}
