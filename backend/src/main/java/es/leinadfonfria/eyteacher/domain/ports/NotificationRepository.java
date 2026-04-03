package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Notification;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository<N extends Notification> {
    N save(N notification, UUID ownerId);
    Optional<N> findById(Long id);

    /**
     * Retrieves a page of notifications for the given owner.
     *
     * @param ownerId The UUID of the owner.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items to return.
     * @return PageResult containing up to {@code size} notifications and a hasNext flag.
     */
    PageResult<N> findByOwnerId(UUID ownerId, int page, int size);

    Optional<N> markAsRead(Long id);
}
