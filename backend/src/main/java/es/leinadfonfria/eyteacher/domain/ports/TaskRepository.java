package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Task;

import java.util.List;

public interface TaskRepository<T extends Task> {
    T findById(Long id);
    List<T> findByTopicId(Long topicId);
    T save(T task, Long topicId);
    boolean existsById(Long id);
    void delete(Long id);
}
