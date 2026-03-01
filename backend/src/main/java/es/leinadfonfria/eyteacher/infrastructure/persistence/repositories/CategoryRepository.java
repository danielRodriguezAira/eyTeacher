package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

    /**
     * Retrieves all distinct categories related to a specific student through their subscribed topics.
     *
     * @param studentId The unique identifier of the student.
     * @return List<CategoryJpaEntity> The list of categories for the student.
     */
    @Query("SELECT DISTINCT c FROM CategoryJpaEntity c JOIN c.topicList t JOIN t.studentList s WHERE s.id = :studentId")
    List<CategoryJpaEntity> findByStudentId(@Param("studentId") UUID studentId);
}
