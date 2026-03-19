package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByOwnerOrderByReadAscCreatedAtDesc(UserJpaEntity owner);
}
