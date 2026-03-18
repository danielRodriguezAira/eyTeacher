package es.leinadfonfria.eyteacher.application.services.solution;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface AddSolutionUseCase {
    Result<Long, Integer> addSolution(AddSolutionRequest request);
}
