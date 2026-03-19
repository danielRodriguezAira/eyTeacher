package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponse;
import es.leinadfonfria.eyteacher.application.dtos.notification.NotificationResponseMapper;
import es.leinadfonfria.eyteacher.application.services.notification.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Notification;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements AddNotificationUseCase, GetNotificationUseCase, GetNotificationsByOwnerUseCase, MarkNotificationAsReadUseCase {
    private final NotificationRepository<Notification> notificationRepository;
    private final NotificationResponseMapper notificationResponseMapper;

    @Override
    @Transactional
    public void addNotification(AddNotificationRequest request) {
        try {
            Notification notification = Notification.create(null, request.message(), request.goTo());
            Notification saved = notificationRepository.save(notification, request.ownerId());
            Result.ok(saved.getId());
        } catch (NotFoundException e) {
            log.error("Not found error during notification creation", e);
            Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during notification creation", e);
            Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public Result<NotificationResponse, Integer> getNotification(Long id) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Notification not found", ErrorCode.NOTIFICATION_NOT_FOUND));
            return Result.ok(notificationResponseMapper.toNotificationResponse(notification));
        } catch (NotFoundException e) {
            log.error("Not found error during notification retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during notification retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public Result<List<NotificationResponse>, Integer> getNotificationsByOwner(UUID ownerId) {
        try {
            List<Notification> notifications = notificationRepository.findByOwnerId(ownerId);
            return Result.ok(notificationResponseMapper.toNotificationResponseList(notifications));
        } catch (NotFoundException e) {
            log.error("Not found error during notifications retrieval by owner", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during notifications retrieval by owner", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public Result<Void, Integer> markNotificationAsRead(Long id) {
        try {
            notificationRepository.markAsRead(id)
                    .orElseThrow(() -> new NotFoundException("Notification not found", ErrorCode.NOTIFICATION_NOT_FOUND));
            return Result.ok(null);
        } catch (NotFoundException e) {
            log.error("Not found error during notification mark as read", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during notification mark as read", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
