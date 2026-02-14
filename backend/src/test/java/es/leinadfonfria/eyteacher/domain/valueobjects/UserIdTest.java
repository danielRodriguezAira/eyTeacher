package es.leinadfonfria.eyteacher.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    @DisplayName("Should create UserId instance when UUID is provided")
    void shouldCreateUserIdWhenUuidIsProvided() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);
        assertEquals(uuid, userId.value());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when UUID is null")
    void shouldThrowExceptionWhenUuidIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }

    @Test
    @DisplayName("Should generate a new random UserId")
    void shouldGenerateRandomUserId() {
        UserId userId = UserId.generate();
        assertNotNull(userId);
        assertNotNull(userId.value());
    }

    @Test
    @DisplayName("Should create UserId from valid string")
    void shouldCreateUserIdFromValidString() {
        String uuidStr = UUID.randomUUID().toString();
        UserId userId = UserId.fromString(uuidStr);
        assertEquals(uuidStr, userId.value().toString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when string is not a valid UUID")
    void shouldThrowExceptionWhenStringIsInvalidUuid() {
        assertThrows(IllegalArgumentException.class, () -> UserId.fromString("invalid-uuid"));
    }
}
