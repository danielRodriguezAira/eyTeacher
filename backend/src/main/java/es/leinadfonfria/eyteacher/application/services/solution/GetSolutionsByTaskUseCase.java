package es.leinadfonfria.eyteacher.application.services.solution;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetSolutionsByTaskUseCase {

    /**
     * Retrieves a page of solutions for the given task.
     * Teachers see all student solutions; students see only their own.
     *
     * @param taskId The ID of the task.
     * @param page   Zero-based page number.
     * @param size   Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of solution responses, or an error code.
     */
    Result<PageResponse<SolutionResponse>, Integer> getSolutionsByTask(Long taskId, int page, int size);
}
