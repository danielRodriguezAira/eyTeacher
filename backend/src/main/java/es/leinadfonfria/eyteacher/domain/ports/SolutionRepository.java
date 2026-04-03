package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface SolutionRepository<S extends Solution> {
    Optional<S> findById(Long id);

    /**
     * Retrieves a page of solutions for the given task.
     *
     * @param taskId  The ID of the task.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items to return.
     * @return PageResult containing up to {@code size} solutions and a hasNext flag.
     */
    PageResult<S> findByTaskId(Long taskId, int page, int size);

    S save(S solution, Long taskId);

    /**
     * Retrieves a page of solutions for the given task submitted by the given student.
     *
     * @param taskId    The ID of the task.
     * @param studentId The UUID of the student.
     * @param page      Zero-based page number.
     * @param size      Maximum number of items to return.
     * @return PageResult containing up to {@code size} solutions and a hasNext flag.
     */
    PageResult<S> findByTaskIdAndStudentId(Long taskId, UUID studentId, int page, int size);
}
