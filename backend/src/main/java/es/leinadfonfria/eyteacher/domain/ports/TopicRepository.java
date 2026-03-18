package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Topic;

import java.util.List;

public interface TopicRepository<T extends Topic> {
    T findById(Long id);
    List<T> findByCategory(Long categoryId);
    List<T> findByCategoryAndStudent(Long categoryId, java.util.UUID studentId);
    T save(T topic);
    boolean existsById(Long id);
    void delete(Long id);
}
