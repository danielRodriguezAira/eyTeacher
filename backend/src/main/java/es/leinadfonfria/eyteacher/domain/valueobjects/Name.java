package es.leinadfonfria.eyteacher.domain.valueobjects;

/**
 * Represents a person's name.
 * This value object ensures that names are not null or empty.
 *
 * @param value The raw name string.
 */
public record Name(String value) {
    /**
     * Validates and creates a Name instance.
     * Used for first names and last names of users.
     *
     * @param value The name string to validate.
     * @throws IllegalArgumentException If the name is null or empty.
     */
    public Name {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }
}
