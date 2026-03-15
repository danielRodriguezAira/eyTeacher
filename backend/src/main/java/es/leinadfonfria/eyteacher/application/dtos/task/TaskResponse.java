package es.leinadfonfria.eyteacher.application.dtos.task;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;

import java.util.List;

public record TaskResponse(
        Long id,
        String description,
        Long topicId,
        List<SolutionResponse> solutionList
) {
}
