package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;

import java.util.List;

public interface CategoryRepository<C extends Category> {

    List<C> findByOwnerId(UserId ownerId);

    List<C> findByStudentId(UserId studentId);

    C findById(Long id);

    C save(C category);

    /**
     * Updates an existing category's own fields (name and description).
     * Retrieves the persisted entity and applies only the provided values,
     * leaving relationships and audit fields untouched.
     *
     * @param category The domain entity carrying the updated values. Must have a non-null id.
     * @return The updated domain entity.
     */
    C update(C category);

    boolean existsById(Long id);
    void delete(Long id);
}
