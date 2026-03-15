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

class TaskTest {

    @Test
    @DisplayName("Debe crear una Task correctamente usando el método create")
    void createTask_Success() {

        // Act
        Task task = Task.create("Solve equations");

        // Assert
        assertNotNull(task);
        assertNull(task.getId());
        assertEquals("Solve equations", task.getDescription());
        assertTrue(task.getSolutionList().isEmpty());
    }

    @Test
    @DisplayName("Debe editar una Task correctamente usando el método edit")
    void editTask_Success() {
        // Arrange
        Long taskId = 1L;

        // Act
        Task task = Task.edit(taskId, "Solve complex equations");

        // Assert
        assertNotNull(task);
        assertEquals(taskId, task.getId());
        assertEquals("Solve complex equations", task.getDescription());
        assertNull(task.getTopic());
        assertTrue(task.getSolutionList().isEmpty());
    }
}
