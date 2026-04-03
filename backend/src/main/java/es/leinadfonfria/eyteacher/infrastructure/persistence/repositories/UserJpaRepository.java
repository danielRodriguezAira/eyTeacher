package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserJpaEntity}.
 * Provides abstraction for database operations on the users table.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    /**
     * Retrieves a user by their email address.
     *
     * @param email The email to search for.
     * @return Optional<UserJpaEntity> The user if found.
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check.
     * @return boolean True if a user exists with that email.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a page of distinct students subscribed to any topic whose category is owned by the given user.
     *
     * @param ownerId  The UUID of the teacher who owns the categories.
     * @param pageable Pageable with offset and limit.
     * @return List of distinct {@link UserJpaEntity} students for the requested page.
     */
    @Query("SELECT DISTINCT s FROM TopicJpaEntity t JOIN t.studentList s WHERE t.category.owner.id = :ownerId")
    List<UserJpaEntity> findStudentsByOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);

    /**
     * Retrieves a page of distinct students for the given teacher owner using explicit LIMIT/OFFSET.
     *
     * @param ownerId The UUID of the teacher who owns the categories.
     * @param offset  Number of rows to skip (= {@code page * size}).
     * @param limit   Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of distinct student {@link UserJpaEntity} for the requested page.
     */
    @Query(value = "SELECT DISTINCT u.* FROM users u JOIN topics_students ts ON ts.student_id = u.id JOIN topics t ON t.id = ts.topic_id JOIN categories c ON c.id = t.category_id WHERE c.owner_id = :ownerId ORDER BY u.id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<UserJpaEntity> findPageStudentsByOwnerId(@Param("ownerId") UUID ownerId, @Param("offset") int offset, @Param("limit") int limit);
}
