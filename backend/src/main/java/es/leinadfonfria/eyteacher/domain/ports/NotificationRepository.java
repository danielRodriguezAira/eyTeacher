package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository<N extends Notification> {
    N save(N notification, UUID ownerId);
    Optional<N> findById(Long id);
    List<N> findByOwnerId(UUID ownerId);
    Optional<N> markAsRead(Long id);
}
