package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface SaveTaskUseCase {
    Result<Long, Integer> saveTask(SaveTaskRequest request);
}
