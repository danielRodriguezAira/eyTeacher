package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.StudentResponse;

import java.util.List;

/**
 * Data transfer object for returning category data.
 *
 * @param id          The category userId.
 * @param name        The category firstName.
 * @param description The category description.
 * @param ownerId     The UUID string of the user who owns this category.
 * @param topicList   The list of topics related to this category.
 */
public record GetCategoryResponse(
        Long id,
        String name,
        String description,
        String ownerId,
        List<GetTopicResponse> topicList,
        List<StudentResponse> studentList
) {
}
