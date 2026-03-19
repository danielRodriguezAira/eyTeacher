package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CorrectionJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CorrectionJpaRepository extends JpaRepository<CorrectionJpaEntity, Long> {
    Optional<CorrectionJpaEntity> findBySolution(SolutionJpaEntity solution);
}
