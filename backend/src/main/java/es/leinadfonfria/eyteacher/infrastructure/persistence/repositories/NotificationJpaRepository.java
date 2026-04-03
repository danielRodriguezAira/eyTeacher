package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByOwnerIdOrderByReadAscCreatedAtDesc(UUID ownerId);

    List<NotificationJpaEntity> findByOwnerIdOrderByReadAscCreatedAtDesc(UUID ownerId, Pageable pageable);

    /**
     * Retrieves a page of notifications for the given owner using explicit LIMIT/OFFSET,
     * ordered so that unread notifications appear before read ones.
     *
     * @param ownerId The owner's UUID.
     * @param offset  Number of rows to skip (= {@code page * size}).
     * @param limit   Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of notification entities ordered by read status asc, creation date desc.
     */
    @Query(value = "SELECT * FROM notifications WHERE owner_id = :ownerId ORDER BY is_read ASC, created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<NotificationJpaEntity> findPageByOwnerId(@Param("ownerId") UUID ownerId, @Param("offset") int offset, @Param("limit") int limit);
}
