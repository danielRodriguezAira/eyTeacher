package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CorrectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrectionJpaRepository extends JpaRepository<CorrectionJpaEntity, Long> {
    Optional<CorrectionJpaEntity> findBySolutionId(Long solution);
}
