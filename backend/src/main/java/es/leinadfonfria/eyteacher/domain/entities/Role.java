package es.leinadfonfria.eyteacher.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of available user roles in the system.
 */
@Getter
@AllArgsConstructor
public enum Role {
    TEACHER("ROLE_TEACHER"),
    STUDENT("ROLE_STUDENT");

    private final String roleName;
}
