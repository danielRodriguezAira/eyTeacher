package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link TopicJpaEntity}.
 * Provides abstraction for database operations on the topics table.
 */
@Repository
public interface TopicJpaRepository extends JpaRepository<TopicJpaEntity, Long> {

    /**
     * Retrieves all topics belonging to a specific category.
     *
     * @param category The category entity.
     * @return List<TopicJpaEntity> The list of topics in the specified category.
     */
    List<TopicJpaEntity> findByCategory(CategoryJpaEntity category);

    @Query("SELECT t FROM TopicJpaEntity t JOIN t.studentList s WHERE s.id = :userId and t.category.id = :categoryId")
    List<TopicJpaEntity> findTopicByCategoryIdAndStudentId(Long categoryId, UUID userId);
}
