package es.leinadfonfria.eyteacher.domain.ports;

import es.leinadfonfria.eyteacher.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository<U extends User> {
    Optional<U> findById(UUID id);
    Optional<U> findByEmail(String email);
    boolean existsByEmail(String email);
    U save(U user);
}
