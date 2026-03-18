package es.leinadfonfria.eyteacher.application.services.solution;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetSolutionsByTaskUseCase {
    Result<List<SolutionResponse>, Integer> getSolutionsByTask(Long taskId);
}
