package es.leinadfonfria.eyteacher.application.dtos.task;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;

/**
 * Represents a single task in the student's task view, including their solution if submitted.
 *
 * @param id          The task ID.
 * @param description The task description.
 * @param solution    The student's solution for this task, or {@code null} if not yet submitted.
 */
public record StudentTaskItem(
        Long id,
        String description,
        SolutionResponse solution
) {}