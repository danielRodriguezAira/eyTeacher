package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should create User using factory method")
    void shouldCreateUserUsingFactoryMethod() {
        UserId id = UserId.generate();
        Email email = new Email("test@example.com");
        String password = "hashedPassword";
        Name firstName = new Name("John");
        Name lastName = new Name("Doe");
        boolean isAdmin = false;

        User user = User.create(id, email, password, firstName, lastName, isAdmin);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertFalse(user.isAdmin());
    }

    @Test
    @DisplayName("Should create User using builder")
    void shouldCreateUserUsingBuilder() {
        UserId id = UserId.generate();
        Email email = new Email("admin@example.com");
        String password = "adminPassword";
        Name firstName = new Name("Admin");
        Name lastName = new Name("User");

        User user = User.builder()
                .id(id)
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .isAdmin(true)
                .build();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertTrue(user.isAdmin());
    }
}
