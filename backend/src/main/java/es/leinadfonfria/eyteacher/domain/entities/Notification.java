package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a Notification in the system.
 *
 * <p>Instead of storing a frontend route, a notification references the type and ID
 * of the entity it concerns ({@link NotificationEntityType} + {@code entityId}).
 * The frontend is responsible for building the navigation link from these two values.</p>
 */
@Getter
@Builder
public class Notification {
    private final Long id;
    private final User owner;
    private final String message;
    private final NotificationEntityType entityType;
    private final Long entityId;
    private final boolean read;

    private Notification(Long id, User owner, String message, NotificationEntityType entityType, Long entityId, boolean read) {
        this.id = id;
        this.owner = owner;
        this.message = message;
        this.entityType = entityType;
        this.entityId = entityId;
        this.read = read;
    }

    /**
     * Creates a new unread notification.
     *
     * @param owner      The user who receives the notification.
     * @param message    The human-readable notification text.
     * @param entityType The type of entity this notification refers to.
     * @param entityId   The ID of the referenced entity.
     * @return A new {@link Notification} instance with {@code read = false}.
     */
    public static Notification create(User owner, String message, NotificationEntityType entityType, Long entityId) {
        return new Notification(null, owner, message, entityType, entityId, false);
    }
}
