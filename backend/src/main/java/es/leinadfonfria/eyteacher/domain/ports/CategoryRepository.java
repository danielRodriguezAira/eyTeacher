package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;

import java.util.List;

public interface CategoryRepository<C extends Category> {

    List<C> findByOwnerId(UserId ownerId);

    List<C> findByStudentId(UserId studentId);

    C findById(Long id);

    C save(C category);
    boolean existsById(Long id);
    int countTopicList(Long id);
    void delete(Long id);
}
