package es.leinadfonfria.eyteacher.application.dtos.task;

import java.util.List;

/**
 * Represents one status group in the student's task view.
 * Tasks are first grouped by status, then sub-grouped by category and topic.
 * The three possible statuses, in display order, are:
 * <ul>
 *   <li>{@code WITHOUT_SOLUTION} – tasks with no solution submitted yet.</li>
 *   <li>{@code WITHOUT_CORRECTION} – tasks with a submitted solution awaiting correction.</li>
 *   <li>{@code CORRECTED} – tasks whose solution has already been corrected.</li>
 * </ul>
 *
 * @param status     The task status label for this group.
 * @param categories The list of category groups containing the tasks in this status.
 */
public record StudentTasksResponse(
        String status,
        List<CategoryTaskGroup> categories
) {}