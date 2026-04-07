package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.config.TestRabbitConfig;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.security.JwtService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link TaskController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestRabbitConfig.class)
class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    // Datos compartidos entre los tests de cada clase anidada
    private UserJpaEntity teacher;
    private TopicJpaEntity topic;
    private TaskJpaEntity task1;
    private TaskJpaEntity task2;

    /**
     * BeforeEach se ejecuta antes de cada test.
     * Creamos la jerarquía completa: profesor → categoría → tema → tareas.
     */
    @BeforeEach
    void setUp() {
        teacher = UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .email("teacher" + UUID.randomUUID() + "@example.com")
                .password("hashed")
                .firstName("Laura")
                .lastName("Martínez")
                .isAdmin(false)
                .build();
        entityManager.persist(teacher);

        CategoryJpaEntity category = CategoryJpaEntity.builder()
                .name("Matemáticas")
                .description("Categoría de matemáticas")
                .owner(teacher)
                .build();
        entityManager.persist(category);

        topic = TopicJpaEntity.builder()
                .name("Álgebra")
                .description("Álgebra básica")
                .category(category)
                .studentList(List.of())
                .build();
        entityManager.persist(topic);

        task1 = TaskJpaEntity.builder()
                .description("Resuelve 2x + 3 = 7")
                .topic(topic)
                .build();
        task2 = TaskJpaEntity.builder()
                .description("Factoriza x² - 4")
                .topic(topic)
                .build();
        entityManager.persist(task1);
        entityManager.persist(task2);

        entityManager.flush();
    }

    // ─── GET /api/v1/tasks/topic/{topicId} ────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/tasks/topic/{topicId}")
    class GetTasksByTopicTests {

        @Test
        @DisplayName("Debe devolver 401 si no se incluye token JWT")
        void getTasksByTopic_NoToken_Returns401() throws Exception {
            // Sin cabecera Authorization, el filtro JWT rechaza la petición
            mockMvc.perform(get("/api/v1/tasks/topic/{topicId}", topic.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Debe devolver la primera página de tareas al profesor propietario")
        void getTasksByTopic_TeacherOwner_ReturnsTasks() throws Exception {
            String token = generateTeacherToken(teacher);

            mockMvc.perform(get("/api/v1/tasks/topic/{topicId}", topic.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    // Verificamos la estructura de PageResponse
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.page", is(0)))
                    .andExpect(jsonPath("$.size", is(10)))
                    .andExpect(jsonPath("$.hasNext", is(false)));
        }

        @Test
        @DisplayName("Debe paginar correctamente cuando hay más tareas que el tamaño de página")
        void getTasksByTopic_Pagination_ReturnsCorrectPage() throws Exception {
            // Insertamos 3 tareas más para tener 5 en total con page size = 3
            for (int i = 3; i <= 5; i++) {
                TaskJpaEntity extra = TaskJpaEntity.builder()
                        .description("Tarea extra " + i)
                        .topic(topic)
                        .build();
                entityManager.persist(extra);
            }
            entityManager.flush();

            String token = generateTeacherToken(teacher);

            // Primera página: 3 elementos, hasNext = true
            mockMvc.perform(get("/api/v1/tasks/topic/{topicId}", topic.getId())
                            .param("page", "0")
                            .param("size", "3")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)))
                    .andExpect(jsonPath("$.hasNext", is(true)));

            // Segunda página: 2 elementos, hasNext = false
            mockMvc.perform(get("/api/v1/tasks/topic/{topicId}", topic.getId())
                            .param("page", "1")
                            .param("size", "3")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.hasNext", is(false)));
        }

        @Test
        @DisplayName("Debe devolver 400 si el profesor no es propietario del tema")
        void getTasksByTopic_OtherTeacher_Returns400() throws Exception {
            // Creamos un segundo profesor que NO es dueño de la categoría
            UserJpaEntity otherTeacher = UserJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .email("other" + UUID.randomUUID() + "@example.com")
                    .password("hashed")
                    .firstName("Carlos")
                    .lastName("López")
                    .isAdmin(false)
                    .build();
            entityManager.persist(otherTeacher);
            entityManager.flush();

            String otherToken = generateTeacherToken(otherTeacher);

            mockMvc.perform(get("/api/v1/tasks/topic/{topicId}", topic.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + otherToken))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── GET /api/v1/tasks/{taskId} ───────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/tasks/{taskId}")
    class GetTaskByIdTests {

        @Test
        @DisplayName("Debe devolver el detalle de la tarea al profesor propietario")
        void getTask_TeacherOwner_ReturnsTask() throws Exception {
            String token = generateTeacherToken(teacher);

            mockMvc.perform(get("/api/v1/tasks/{taskId}", task1.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(task1.getId().intValue())))
                    .andExpect(jsonPath("$.description", is("Resuelve 2x + 3 = 7")));
        }

        @Test
        @DisplayName("Debe devolver 400 si la tarea no existe")
        void getTask_NotFound_Returns400() throws Exception {
            String token = generateTeacherToken(teacher);

            mockMvc.perform(get("/api/v1/tasks/{taskId}", 99999L)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Genera un token JWT de tipo TEACHER para el usuario dado.
     * Construye el objeto de dominio {@link User} mínimo que {@link JwtService} necesita.
     *
     * @param userEntity La entidad JPA del usuario.
     * @return El token JWT en formato String.
     */
    private String generateTeacherToken(UserJpaEntity userEntity) {
        User user = User.create(
                new UserId(userEntity.getId()),
                new Email(userEntity.getEmail()),
                Password.hashed(userEntity.getPassword()),
                new Name(userEntity.getFirstName()),
                new Name(userEntity.getLastName()),
                userEntity.isAdmin()
        );
        return jwtService.generateToken(user, Role.TEACHER);
    }
}
