package es.leinadfonfria.eyteacher.application.dtos.solution;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;

public record SolutionResponse(
        Long id,
        String description,
        UserResponse student,
        TaskResponse task
) {
}
