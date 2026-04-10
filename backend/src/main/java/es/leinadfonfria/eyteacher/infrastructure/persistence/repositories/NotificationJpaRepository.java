package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByOwnerIdOrderByReadAscCreatedAtDesc(UUID ownerId);

    /**
     * Retrieves a page of notifications for the given owner filtered by entity type,
     * using explicit LIMIT/OFFSET and ordered so that unread notifications appear first.
     *
     * @param ownerId The owner's UUID.
     * @param types   Allowed entity type names (e.g. {@code ["TASK", "CORRECTION", "TOPIC"]}).
     * @param offset  Number of rows to skip (= {@code page * size}).
     * @param limit   Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of notification entities ordered by read status asc, creation date desc.
     */
    @Query(value = "SELECT * FROM notifications WHERE owner_id = :ownerId AND entity_type IN (:types) ORDER BY is_read ASC, created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<NotificationJpaEntity> findPageByOwnerIdAndEntityTypes(@Param("ownerId") UUID ownerId, @Param("types") List<String> types, @Param("offset") int offset, @Param("limit") int limit);
}
