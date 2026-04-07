package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;

import java.util.List;
import java.util.UUID;

public interface TaskRepository<T extends Task> {
    T findById(Long id);

    /**
     * Retrieves a page of tasks belonging to the given topic.
     *
     * @param topicId The ID of the topic.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items to return.
     * @return PageResult containing up to {@code size} tasks and a hasNext flag.
     */
    PageResult<T> findByTopicId(Long topicId, int page, int size);

    /**
     * Retrieves all tasks belonging to topics in which the given student is enrolled.
     * Each returned task contains only the solution submitted by that student, if any.
     *
     * @param studentId The UUID of the student.
     * @return List of tasks accessible to the student with their individual solution loaded.
     */
    List<T> findByStudentId(UUID studentId);

    /**
     * Retrieves all tasks belonging to topics in which the given student is enrolled,
     * restricted to categories owned by the given teacher.
     *
     * @param studentId The UUID of the student.
     * @param teacherId The UUID of the teacher who owns the categories.
     * @return List of tasks accessible to the student within the teacher's categories.
     */
    List<T> findByStudentIdAndTeacherId(UUID studentId, UUID teacherId);

    T save(T task);

    /**
     * Updates an existing task's own fields (description).
     * Retrieves the persisted entity and applies only the provided values,
     * leaving the topic relation, solutions and audit fields untouched.
     *
     * @param task    The domain entity carrying the updated values. Must have a non-null id.
     * @return The updated domain entity.
     */
    T update(T task);

    boolean existsById(Long id);

    /**
     * Checks whether any task exists for the given topic.
     *
     * @param topicId The topic identifier.
     * @return {@code true} if at least one task belongs to the topic, {@code false} otherwise.
     */
    boolean existsByTopicId(Long topicId);

    void delete(Long id);
}
