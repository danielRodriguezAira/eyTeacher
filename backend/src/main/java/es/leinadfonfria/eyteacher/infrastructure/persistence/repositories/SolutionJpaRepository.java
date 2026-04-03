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
}
