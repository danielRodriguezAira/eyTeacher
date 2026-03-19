package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Notification;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.NotificationRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.NotificationMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.NotificationJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository<Notification> {
    private final NotificationJpaRepository notificationJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification, UUID ownerId) {
        UserJpaEntity ownerEntity = userJpaRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Notification not found", ErrorCode.NOTIFICATION_NOT_FOUND));
        NotificationJpaEntity entity = notificationMapper.toEntity(notification);
        entity.setOwner(ownerEntity);
        return notificationMapper.toDomain(notificationJpaRepository.save(entity));
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByOwnerId(UUID ownerId) {
        UserJpaEntity ownerEntity = userJpaRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found", ErrorCode.USER_NOT_FOUND));
        return notificationJpaRepository.findByOwnerOrderByReadAscCreatedAtDesc(ownerEntity)
                .stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Notification> markAsRead(Long id) {
        NotificationJpaEntity notificationJpaEntity = notificationJpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found", ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationJpaEntity.setRead(true);
        return Optional.of(notificationJpaRepository.save(notificationJpaEntity))
                .map(notificationMapper::toDomain);
    }
}
