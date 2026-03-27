package es.leinadfonfria.eyteacher.application.dtos.task;

import java.util.List;

/**
 * Groups topic task groups belonging to a specific category within the student's task view.
 *
 * @param id     The category ID.
 * @param name   The category name.
 * @param topics The list of topic groups within this category.
 */
public record CategoryTaskGroup(
        Long id,
        String name,
        List<TopicTaskGroup> topics
) {}