package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository<T extends Task> {
    T findById(Long id);
    List<T> findByTopicId(Long topicId);

    /**
     * Retrieves all tasks belonging to topics in which the given student is enrolled.
     * Each returned task contains only the solution submitted by that student, if any.
     *
     * @param studentId The UUID of the student.
     * @return List of tasks accessible to the student with their individual solution loaded.
     */
    List<T> findByStudentId(UUID studentId);

    T save(T task, Long topicId);
    boolean existsById(Long id);
    void delete(Long id);
}
