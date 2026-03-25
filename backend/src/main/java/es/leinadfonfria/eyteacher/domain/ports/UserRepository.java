package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository<U extends User> {
    Optional<U> findById(UUID id);
    Optional<U> findByEmail(String email);
    boolean existsByEmail(String email);
    U save(U user);

    /**
     * Retrieves all distinct students subscribed to any topic whose category is owned by the given user.
     *
     * @param ownerId The UUID of the teacher who owns the categories.
     * @return List of students found across all topics of the owner's categories.
     */
    List<U> findStudentsByOwnerId(UUID ownerId);
}
