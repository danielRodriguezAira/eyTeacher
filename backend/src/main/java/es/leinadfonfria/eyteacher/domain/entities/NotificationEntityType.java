package es.leinadfonfria.eyteacher.domain.entities;

/**
 * Identifies the type of entity that a {@link Notification} references,
 * allowing the frontend to construct the appropriate navigation link
 * without coupling the backend to any frontend routing convention.
 */
public enum NotificationEntityType {
    /** A task assigned to a student. */
    TASK,
    /** A solution submitted by a student. */
    SOLUTION,
    /** A correction submitted by a teacher. */
    CORRECTION,
    /** A topic to which a student has been subscribed. */
    TOPIC
}
