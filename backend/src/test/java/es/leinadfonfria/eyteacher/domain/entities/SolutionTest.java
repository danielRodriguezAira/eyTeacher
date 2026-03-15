package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    @Test
    @DisplayName("Debe crear una Solution correctamente usando el método create")
    void createSolution_Success() {
        // Arrange
        User teacher = User.create(
                new UserId(UUID.randomUUID()),
                new Email("teacher@example.com"),
                Password.hashed("password"),
                new Name("John"),
                new Name("Doe"),
                false
        );
        User student = User.create(
                new UserId(UUID.randomUUID()),
                new Email("student@example.com"),
                Password.hashed("password"),
                new Name("Jane"),
                new Name("Smith"),
                false
        );
        Category category = Category.create(new Name("Math"), "Math category", teacher);
        Task task = Task.create("Solve equations");

        // Act
        Solution solution = Solution.create("My solution description", student, task);

        // Assert
        assertNotNull(solution);
        assertNull(solution.getId());
        assertEquals("My solution description", solution.getDescription());
        assertEquals(student, solution.getStudent());
        assertEquals(task, solution.getTask());
    }
}
