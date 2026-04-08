package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Topic;

import java.util.List;

public interface TopicRepository<T extends Topic> {
    T findById(Long id);
    List<T> findByCategory(Long categoryId);
    List<T> findByCategoryAndStudent(Long categoryId, java.util.UUID studentId);
    T save(T topic);

    /**
     * Updates an existing topic's own fields (name, description, category and studentList).
     * Retrieves the persisted entity and applies only the provided values,
     * leaving audit fields and task list untouched.
     *
     * @param topic The domain entity carrying the updated values. Must have a non-null id.
     * @return The updated domain entity.
     */
    T update(T topic);

    boolean existsById(Long id);

    boolean existsByCategoryId(Long categoryId);

    void delete(Long id);

    T updateStudents(Topic updatedTopic);
}
