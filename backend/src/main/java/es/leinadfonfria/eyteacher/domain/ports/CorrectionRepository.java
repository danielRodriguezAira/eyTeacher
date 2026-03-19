package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Correction;
import java.util.Optional;

public interface CorrectionRepository<C extends Correction> {
    C save(C correction, Long solutionId);
    Optional<C> findBySolutionId(Long solutionId);
}
