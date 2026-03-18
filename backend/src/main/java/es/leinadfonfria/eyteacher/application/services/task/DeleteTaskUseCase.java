package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface DeleteTaskUseCase {
    Result<Void, Integer> deleteTask(Long id);
}
