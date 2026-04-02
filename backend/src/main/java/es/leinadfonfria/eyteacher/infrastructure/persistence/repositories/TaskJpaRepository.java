package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {

    Optional<List<TaskJpaEntity>> findByTopic(TopicJpaEntity topic);

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
