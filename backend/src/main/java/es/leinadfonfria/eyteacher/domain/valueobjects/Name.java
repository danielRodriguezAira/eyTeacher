package es.leinadfonfria.eyteacher.domain.valueobjects;

/**
 * Represents a person's firstName.
 * This value object ensures that names are not null or empty.
 *
 * @param value The raw firstName string.
 */
public record Name(String value) {

    private static final int MAX_LENGTH = 255;

    /**
     * Validates and creates a Name instance.
     * Used for first names and last names of users.
     *
     * @param value The firstName string to validate.
     * @throws IllegalArgumentException If the firstName is null or empty.
     */
    public Name {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if(value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Name cannot be longer than %d characters", MAX_LENGTH));
        }
    }
}
