package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    @DisplayName("Debe crear un Topic correctamente usando el método create")
    void createTopic_Success() {
        // Arrange
        User owner = User.create(
                new UserId(UUID.randomUUID()),
                new Email("test@example.com"),
                Password.hashed("password"),
                new Name("John"),
                new Name("Doe"),
                false
        );
        Category category = Category.create("Math", "Math category", owner);

        // Act
        Topic topic = Topic.create("Algebra", "Basic algebra", category);

        // Assert
        assertNotNull(topic);
        assertNull(topic.getId());
        assertEquals("Algebra", topic.getName());
        assertEquals("Basic algebra", topic.getDescription());
        assertEquals(category, topic.getCategory());
    }

    @Test
    @DisplayName("Debe editar un Topic correctamente usando el método edit")
    void editTopic_Success() {
        // Arrange
        User owner = User.create(
                new UserId(UUID.randomUUID()),
                new Email("test@example.com"),
                Password.hashed("password"),
                new Name("John"),
                new Name("Doe"),
                false
        );
        Category category = Category.create("Math", "Math category", owner);
        Long topicId = 1L;

        // Act
        Topic topic = Topic.edit(topicId, "Calculus", "Advanced calculus", category);

        // Assert
        assertNotNull(topic);
        assertEquals(topicId, topic.getId());
        assertEquals("Calculus", topic.getName());
        assertEquals("Advanced calculus", topic.getDescription());
        assertEquals(category, topic.getCategory());
    }
}
