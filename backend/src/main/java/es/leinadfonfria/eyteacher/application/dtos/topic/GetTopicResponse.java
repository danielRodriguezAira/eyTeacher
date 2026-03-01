package es.leinadfonfria.eyteacher.application.dtos.topic;

import java.util.List;

/**
 * Data transfer object for returning topic data.
 *
 * @param id          The topic userId.
 * @param name        The topic firstName.
 * @param description The topic description.
 * @param categoryId  The ID of the category this topic belongs to.
 */
public record GetTopicResponse(
        Long id,
        String name,
        String description,
        Long categoryId,
        List<StudentResponse> studentList
) {
}
