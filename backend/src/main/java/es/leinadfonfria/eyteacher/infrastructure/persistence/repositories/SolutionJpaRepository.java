package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolutionJpaRepository extends JpaRepository<SolutionJpaEntity, Long> {
    List<SolutionJpaEntity> findByTask(TaskJpaEntity task);

    @Query("SELECT s FROM SolutionJpaEntity s WHERE s.task = :task AND s.student.id = :studentId")
    List<SolutionJpaEntity> findByTaskAndStudentId(TaskJpaEntity task, UUID studentId);
}
