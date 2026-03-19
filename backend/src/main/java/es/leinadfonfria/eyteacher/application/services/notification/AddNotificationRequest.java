package es.leinadfonfria.eyteacher.application.services.notification;

import java.util.UUID;

public record AddNotificationRequest(UUID ownerId, String message, String goTo) {}
