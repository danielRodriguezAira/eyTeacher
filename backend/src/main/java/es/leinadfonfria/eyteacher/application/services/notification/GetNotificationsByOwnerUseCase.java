package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponse;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Role;

import java.util.UUID;

public interface GetNotificationsByOwnerUseCase {

    /**
     * Retrieves a page of notifications for the given owner, filtered by the types
     * that are visible to the caller's role.
     *
     * @param ownerId The UUID of the notification owner.
     * @param role    The role of the authenticated user, used to determine which
     *                {@link es.leinadfonfria.eyteacher.domain.entities.NotificationEntityType}s are allowed.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of notification responses, or an error code.
     */
    Result<PageResponse<NotificationResponse>, Integer> getNotificationsByOwner(UUID ownerId, Role role, int page, int size);
}
