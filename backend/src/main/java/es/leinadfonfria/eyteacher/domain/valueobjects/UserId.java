package es.leinadfonfria.eyteacher.domain.valueobjects;

import java.util.UUID;

/**
 * Represents the unique identifier of a user.
 * This value object wraps a UUID to provide type-safe identification.
 *
 * @param value The UUID value.
 */
public record UserId(UUID value) {
    /**
     * Validates and creates a UserId instance.
     *
     * @param value The UUID to validate.
     * @throws IllegalArgumentException If the value is null.
     */
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    /**
     * Generates a new random UserId.
     * Used when creating a new user in the system.
     *
     * @return UserId A new random identifier.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Creates a UserId from its string representation.
     * Used when reconstructing an ID from an external source or database.
     *
     * @param id The string representation of the UUID.
     * @return UserId The identifier instance.
     * @throws IllegalArgumentException If the string is not a valid UUID.
     */
    public static UserId fromString(String id) {
        return new UserId(UUID.fromString(id));
    }
}
