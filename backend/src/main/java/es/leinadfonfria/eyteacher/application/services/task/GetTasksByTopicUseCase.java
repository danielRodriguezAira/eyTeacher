package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetTasksByTopicUseCase {

    /**
     * Retrieves a page of tasks for the given topic.
     *
     * @param topicId The ID of the topic.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of task responses, or an error code.
     */
    Result<PageResponse<TaskResponse>, Integer> getTasksByTopic(Long topicId, int page, int size);
}
