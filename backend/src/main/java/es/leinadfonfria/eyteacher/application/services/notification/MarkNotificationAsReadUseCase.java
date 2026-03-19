package es.leinadfonfria.eyteacher.application.services.notification;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface MarkNotificationAsReadUseCase {
    Result<Void, Integer> markNotificationAsRead(Long id);
}
