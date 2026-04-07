package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {

    /**
     * Checks whether any task exists for the given topic.
     *
     * @param topicId The topic identifier.
     * @return {@code true} if at least one task belongs to the topic, {@code false} otherwise.
     */
    boolean existsByTopicId(Long topicId);

    /**
     * Retrieves a page of tasks for the given topic using explicit LIMIT/OFFSET,
     * avoiding the Spring Data offset miscalculation when using {@code size+1} trick.
     *
     * @param topicId The topic identifier.
     * @param offset  Number of rows to skip (= {@code page * size}).
     * @param limit   Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of task entities ordered by creation date descending.
     */
    @Query(value = "SELECT * FROM tasks WHERE topic_id = :topicId ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<TaskJpaEntity> findPageByTopicId(@Param("topicId") Long topicId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * Retrieves all tasks that belong to topics in which the given student is enrolled.
     *
     * @param studentId The UUID of the student.
     * @return List of tasks accessible to the student.
     */
    @Query("SELECT DISTINCT t FROM TaskJpaEntity t JOIN t.topic.studentList s WHERE s.id = :studentId")
    List<TaskJpaEntity> findByStudentId(@Param("studentId") UUID studentId);

    /**
     * Retrieves all tasks that belong to topics in which the given student is enrolled,
     * restricted to topics whose category is owned by the given teacher.
     *
     * @param studentId The UUID of the student.
     * @param teacherId The UUID of the teacher who owns the category.
     * @return List of tasks accessible to the student within the teacher's categories.
     */
    @Query("SELECT DISTINCT t FROM TaskJpaEntity t JOIN t.topic.studentList s WHERE s.id = :studentId AND t.topic.category.owner.id = :teacherId")
    List<TaskJpaEntity> findByStudentIdAndTeacherId(@Param("studentId") UUID studentId, @Param("teacherId") UUID teacherId);
}
