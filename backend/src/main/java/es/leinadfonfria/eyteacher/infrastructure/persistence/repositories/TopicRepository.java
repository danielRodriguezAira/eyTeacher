package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link TopicJpaEntity}.
 * Provides abstraction for database operations on the topics table.
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicJpaEntity, Long> {

    /**
     * Retrieves all topics belonging to a specific category.
     *
     * @param category The category entity.
     * @return List<TopicJpaEntity> The list of topics in the specified category.
     */
    List<TopicJpaEntity> findByCategory(CategoryJpaEntity category);
}
