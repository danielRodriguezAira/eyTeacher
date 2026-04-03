package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByOwnerIdOrderByReadAscCreatedAtDesc(UUID ownerId);

    List<NotificationJpaEntity> findByOwnerIdOrderByReadAscCreatedAtDesc(UUID ownerId, Pageable pageable);
}
