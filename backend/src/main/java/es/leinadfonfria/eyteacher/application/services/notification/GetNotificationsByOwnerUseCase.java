package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;
import java.util.UUID;

public interface GetNotificationsByOwnerUseCase {
    Result<List<NotificationResponse>, Integer> getNotificationsByOwner(UUID ownerId);
}
