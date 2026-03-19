package es.leinadfonfria.eyteacher.application.services.solution;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetSolutionByIdUseCase {
    Result<SolutionResponse, Integer> getSolutionById(Long id);
}
