package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
