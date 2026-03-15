package es.leinadfonfria.eyteacher.application.dtos.topic;

/**
 * Data transfer object for editing and adding a new topic.
 *
 * @param id          The topic id. null if new topic.
 * @param name        The topic firstName.
 * @param description The topic description.
 * @param categoryId  The ID of the category this topic belongs to.
 */
public record SaveTopicRequest(
        Long id,
        String name,
        String description,
        Long categoryId
) {}
