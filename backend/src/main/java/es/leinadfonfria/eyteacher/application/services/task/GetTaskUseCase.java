package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetTaskUseCase {
    Result<TaskResponse, Integer> getTask(Long id);
}
