package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a Notification in the system.
 */
@Getter
@Builder
public class Notification {
    private final Long id;
    private final User owner;
    private final String message;
    private final String goTo;
    private final boolean read;

    private Notification(Long id, User owner, String message, String goTo, boolean read) {
        this.id = id;
        this.owner = owner;
        this.message = message;
        this.goTo = goTo;
        this.read = read;
    }

    public static Notification create(User owner, String message, String goTo) {
        return new Notification(null, owner, message, goTo, false);
    }
}
