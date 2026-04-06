package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        Category category = Category.create(new Name("Math"), "Math category", owner);

        // Act
        Topic topic = Topic.create(new Name("Algebra"), "Basic algebra", category, List.of());

        // Assert
        assertNotNull(topic);
        assertNull(topic.getId());
        assertEquals("Algebra", topic.getName().value());
        assertEquals("Basic algebra", topic.getDescription());
        assertEquals(category, topic.getCategory());
        assertTrue(topic.getStudentList().isEmpty());
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
        Category category = Category.create(new Name("Math"), "Math category", owner);
        Long topicId = 1L;

        // Act
        Topic topic = Topic.edit(topicId, new Name("Calculus"), "Advanced calculus");

        // Assert
        assertNotNull(topic);
        assertEquals(topicId, topic.getId());
        assertEquals("Calculus", topic.getName().value());
        assertEquals("Advanced calculus", topic.getDescription());
        assertNull(topic.getCategory());
        assertTrue(topic.getStudentList().isEmpty());
        assertTrue(topic.getTaskList().isEmpty());
    }
}
