package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;

import java.util.List;

/**
 * Data transfer object for returning category data.
 *
 * @param id          The category id.
 * @param name        The category name.
 * @param description The category description.
 * @param ownerId     The UUID string of the user who owns this category.
 * @param topicList   The list of topics related to this category.
 */
public record GetCategoryResponse(
        Long id,
        String name,
        String description,
        String ownerId,
        List<GetTopicResponse> topicList
) {
}
