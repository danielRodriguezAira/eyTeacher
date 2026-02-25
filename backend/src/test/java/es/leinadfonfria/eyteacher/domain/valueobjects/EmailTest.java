package es.leinadfonfria.eyteacher.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    @DisplayName("Should create Email instance when value is valid")
    void shouldCreateEmailWhenValueIsValid() {
        String validEmail = "test@example.com";
        Email email = new Email(validEmail);
        assertEquals(validEmail, email.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-email", "test@", "@example.com", "test@example"})
    @DisplayName("Should throw IllegalArgumentException when email format is invalid")
    void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }
}
