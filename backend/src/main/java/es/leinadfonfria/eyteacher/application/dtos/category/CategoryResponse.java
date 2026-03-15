package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;

import java.util.List;

/**
 * Data transfer object for returning category data.
 *
 * @param id          The category id.
 * @param name        The category firstName.
 * @param description The category description.
 * @param ownerId     The UUID string of the user who owns this category.
 * @param topicList   The list of topics related to this category.
 */
public record CategoryResponse(
        Long id,
        String name,
        String description,
        String ownerId,
        List<TopicResponse> topicList,
        List<UserResponse> studentList
) {
}
