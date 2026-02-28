package es.leinadfonfria.eyteacher.application.dtos.topic;

/**
 * Data transfer object for returning topic data.
 *
 * @param id          The topic id.
 * @param name        The topic name.
 * @param description The topic description.
 * @param categoryId  The ID of the category this topic belongs to.
 */
public record GetTopicResponse(
        Long id,
        String name,
        String description,
        Long categoryId
) {
}
