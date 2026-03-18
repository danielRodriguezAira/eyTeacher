package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetTasksByTopicUseCase {
    Result<List<TaskResponse>, Integer> getTasksByTopic(Long topicId);
}
