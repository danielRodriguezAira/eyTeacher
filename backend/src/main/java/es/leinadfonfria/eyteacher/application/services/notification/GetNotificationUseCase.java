package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetNotificationUseCase {
    Result<NotificationResponse, Integer> getNotification(Long id);
}
