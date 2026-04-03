package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponse;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.UUID;

public interface GetNotificationsByOwnerUseCase {

    /**
     * Retrieves a page of notifications for the given owner.
     *
     * @param ownerId The UUID of the notification owner.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of notification responses, or an error code.
     */
    Result<PageResponse<NotificationResponse>, Integer> getNotificationsByOwner(UUID ownerId, int page, int size);
}
