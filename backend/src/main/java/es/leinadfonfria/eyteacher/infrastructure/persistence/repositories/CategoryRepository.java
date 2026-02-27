package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CategoryJpaEntity}.
 * Provides abstraction for database operations on the categories table.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    /**
     * Retrieves all categories belonging to a specific owner.
     *
     * @param owner The owner user entity.
     * @return List<CategoryJpaEntity> The list of categories owned by the user.
     */
    List<CategoryJpaEntity> findByOwner(UserJpaEntity owner);
}
