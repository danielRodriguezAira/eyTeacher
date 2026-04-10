package es.leinadfonfria.eyteacher.domain.entities;

import java.util.Arrays;
import java.util.List;

/**
 * Identifies the type of entity that a {@link Notification} references,
 * allowing the frontend to construct the appropriate navigation link
 * without coupling the backend to any frontend routing convention.
 *
 * <p>Each type is bound to the {@link Role} of the user who should receive it:
 * students receive task, correction and topic notifications; teachers receive
 * solution notifications.</p>
 */
public enum NotificationEntityType {
    /** A task assigned to a student. */
    TASK(Role.STUDENT),
    /** A solution submitted by a student. */
    SOLUTION(Role.TEACHER),
    /** A correction submitted by a teacher. */
    CORRECTION(Role.STUDENT),
    /** A topic to which a student has been subscribed. */
    TOPIC(Role.STUDENT);

    private final Role targetRole;

    NotificationEntityType(Role targetRole) {
        this.targetRole = targetRole;
    }

    /**
     * Returns the notification types that the given role is allowed to see.
     *
     * @param role The role of the authenticated user.
     * @return An immutable list of {@link NotificationEntityType} values whose target role matches.
     */
    public static List<NotificationEntityType> allowedFor(Role role) {
        return Arrays.stream(values())
                .filter(type -> type.targetRole == role)
                .toList();
    }
}
