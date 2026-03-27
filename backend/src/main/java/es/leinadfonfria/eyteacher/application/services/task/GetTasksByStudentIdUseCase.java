package es.leinadfonfria.eyteacher.application.services.task;

import es.leinadfonfria.eyteacher.application.dtos.task.StudentTasksResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

/**
 * Use case for retrieving all tasks accessible to a student, grouped by status, category, and topic.
 * The result is ordered as follows:
 * <ol>
 *   <li>By status: without solution → without correction → corrected.</li>
 *   <li>Within each status: by category.</li>
 *   <li>Within each category: by topic.</li>
 * </ol>
 */
public interface GetTasksByStudentIdUseCase {

    /**
     * Retrieves all tasks for the given student, grouped by status, then by category, then by topic.
     *
     * @param studentId The string representation of the student's UUID.
     * @return {@link Result} containing a list of {@link StudentTasksResponse} grouped by status,
     *         or an error code on failure.
     */
    Result<List<StudentTasksResponse>, Integer> getTasksByStudentId(String studentId);
}
