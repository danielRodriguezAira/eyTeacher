package es.leinadfonfria.eyteacher.application.dtos.topic;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;

import java.util.List;

/**
 * Data transfer object for returning topic data.
 *
 * @param id          The topic id.
 * @param name        The topic firstName.
 * @param description The topic description.
 * @param categoryId  The ID of the category this topic belongs to.
 */
public record TopicResponse(
        Long id,
        String name,
        String description,
        Long categoryId,
        List<UserResponse> studentList,
        List<TaskResponse> taskList
) {
}
