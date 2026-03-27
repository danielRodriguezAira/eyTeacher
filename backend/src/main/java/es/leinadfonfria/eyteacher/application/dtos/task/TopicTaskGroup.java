package es.leinadfonfria.eyteacher.application.dtos.task;

import java.util.List;

/**
 * Groups tasks belonging to a specific topic within the student's task view.
 *
 * @param id    The topic ID.
 * @param name  The topic name.
 * @param tasks The list of tasks within this topic for the student.
 */
public record TopicTaskGroup(
        Long id,
        String name,
        List<StudentTaskItem> tasks
) {}