package es.leinadfonfria.eyteacher.domain.valueobjects;

import java.util.regex.Pattern;

/**
 * Represents a valid email address.
 * This value object ensures that the email follows a standard format.
 *
 * @param value The raw email string.
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_LENGTH = 255;
    /**
     * Validates and creates an Email instance.
     * Used when creating a user or updating their contact information.
     *
     * @param value The email address to validate.
     * @throws IllegalArgumentException If the email is null, empty, or has an invalid format.
     */
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if(value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Email cannot be longer than %d characters", MAX_LENGTH));
        }
    }
}
