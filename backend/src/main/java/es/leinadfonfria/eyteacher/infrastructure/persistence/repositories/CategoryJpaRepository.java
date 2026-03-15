package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CategoryJpaEntity}.
 * Provides abstraction for database operations on the categories table.
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    /**
     * Retrieves all categories belonging to a specific owner.
     *
     * @param ownerId The owner user entity.
     * @return List<CategoryJpaEntity> The list of categories owned by the user.
     */
    Optional<List<CategoryJpaEntity>> findByOwnerId(UUID ownerId);

    /**
     * Retrieves all distinct categories related to a specific student through their subscribed topics.
     *
     * @param studentId The unique identifier of the student.
     * @return List<CategoryJpaEntity> The list of categories for the student.
     */
    @Query("SELECT DISTINCT c FROM CategoryJpaEntity c JOIN c.topicList t JOIN t.studentList s WHERE s.id = :studentId")
    Optional<List<CategoryJpaEntity>> findByStudentId(@Param("studentId") UUID studentId);

    int countTopicListById(Long categoryId);
}
