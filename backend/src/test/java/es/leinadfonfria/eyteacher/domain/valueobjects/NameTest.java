package es.leinadfonfria.eyteacher.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NameTest {

    @Test
    @DisplayName("Should create Name instance when value is valid")
    void shouldCreateNameWhenValueIsValid() {
        String validName = "John";
        Name name = new Name(validName);
        assertEquals(validName, name.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw IllegalArgumentException when name is empty or blank")
    void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Name(null));
    }
}
