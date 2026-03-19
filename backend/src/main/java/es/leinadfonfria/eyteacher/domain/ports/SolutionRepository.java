package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Solution;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolutionRepository<S extends Solution> {
    Optional<S> findById(Long id);
    List<S> findByTaskId(Long taskId);
    S save(S solution, Long taskId);
    List<Solution> findByTaskIdAndStudentId(Long taskId, UUID studentId);
}
