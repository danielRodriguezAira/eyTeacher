package es.leinadfonfria.eyteacher.domain.valueobjects;

import es.leinadfonfria.eyteacher.domain.errors.AuthException;

import java.util.regex.Pattern;

import static es.leinadfonfria.eyteacher.domain.errors.ErrorCode.INVALID_CREDENTIALS;

/**
 * Password Value Object.
 * - Allows representing passwords in plaintext (for rule validation) and hashed passwords (for persistence).
 * - Rule validation ONLY applies when the password is not hashed.
 */
public record Password(String value, boolean hashed) {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );
    private static final int MAX_LENGTH = 255;

    public Password {
        if (value == null) {
            throw new AuthException("Password cannot be null", INVALID_CREDENTIALS);
        }
        if (!hashed && !PASSWORD_PATTERN.matcher(value).matches()) {
            throw new AuthException("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&., etc)", INVALID_CREDENTIALS);
        }
        if(value.length() > MAX_LENGTH) {
            throw new AuthException(String.format("Email cannot be longer than %d characters", MAX_LENGTH), INVALID_CREDENTIALS);
        }
    }

    public static Password raw(String value) {
        return new Password(value, false);
    }

    public static Password hashed(String hash) {
        return new Password(hash, true);
    }
}
