package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository<U extends User> {
    Optional<U> findById(UUID id);
    Optional<U> findByEmail(String email);
    boolean existsByEmail(String email);
    U save(U user);

    /**
     * Retrieves a page of distinct students subscribed to any topic whose category is owned by the given user.
     *
     * @param ownerId The UUID of the teacher who owns the categories.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items to return.
     * @return PageResult containing up to {@code size} students and a hasNext flag.
     */
    PageResult<U> findStudentsByOwnerId(UUID ownerId, int page, int size);
}
