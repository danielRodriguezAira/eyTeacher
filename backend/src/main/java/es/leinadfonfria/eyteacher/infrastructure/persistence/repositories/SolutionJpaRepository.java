package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolutionJpaRepository extends JpaRepository<SolutionJpaEntity, Long> {

    List<SolutionJpaEntity> findByTaskIdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    @Query("SELECT s FROM SolutionJpaEntity s WHERE s.task.id = :taskId AND s.student.id = :studentId ORDER BY s.createdAt DESC")
    List<SolutionJpaEntity> findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") UUID studentId);

    @Query("SELECT s FROM SolutionJpaEntity s WHERE s.task.id = :taskId AND s.student.id = :studentId ORDER BY s.createdAt DESC")
    List<SolutionJpaEntity> findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") UUID studentId, Pageable pageable);

    /**
     * Retrieves a page of solutions for the given task using explicit LIMIT/OFFSET.
     *
     * @param taskId  The task identifier.
     * @param offset  Number of rows to skip (= {@code page * size}).
     * @param limit   Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of solution entities ordered by creation date descending.
     */
    @Query(value = "SELECT * FROM solutions WHERE task_id = :taskId ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<SolutionJpaEntity> findPageByTaskId(@Param("taskId") Long taskId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * Retrieves a page of solutions for the given task and student using explicit LIMIT/OFFSET.
     *
     * @param taskId    The task identifier.
     * @param studentId The student identifier.
     * @param offset    Number of rows to skip (= {@code page * size}).
     * @param limit     Maximum rows to fetch (= {@code size + 1} to detect next page).
     * @return List of solution entities ordered by creation date descending.
     */
    @Query(value = "SELECT * FROM solutions WHERE task_id = :taskId AND student_id = :studentId ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<SolutionJpaEntity> findPageByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") UUID studentId, @Param("offset") int offset, @Param("limit") int limit);
}
