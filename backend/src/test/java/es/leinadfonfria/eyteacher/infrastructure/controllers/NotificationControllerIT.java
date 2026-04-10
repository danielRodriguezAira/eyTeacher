package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.config.TestRabbitConfig;
import es.leinadfonfria.eyteacher.domain.entities.NotificationEntityType;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link NotificationController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestRabbitConfig.class)
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    private UserJpaEntity owner;
    private String ownerToken;

    @BeforeEach
    void setUp() {
        owner = UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .email("user" + UUID.randomUUID() + "@example.com")
                .password("hashed")
                .firstName("María")
                .lastName("Sánchez")
                .isAdmin(false)
                .build();
        entityManager.persist(owner);
        entityManager.flush();

        ownerToken = generateToken(owner, Role.TEACHER);
    }

    // ─── GET /api/v1/notifications/owner/{ownerId} ────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/notifications/owner/{ownerId}")
    class GetNotificationsByOwnerTests {

        @Test
        @DisplayName("Debe devolver 401 sin token JWT")
        void getNotifications_NoToken_Returns401() throws Exception {
            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Debe devolver página vacía cuando el usuario no tiene notificaciones")
        void getNotifications_NoNotifications_ReturnsEmptyPage() throws Exception {
            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.hasNext", is(false)));
        }

        @Test
        @DisplayName("Debe devolver las notificaciones no leídas antes que las leídas")
        void getNotifications_Mixed_ReturnsUnreadFirst() throws Exception {
            // El token es de TEACHER, que solo ve SOLUTION
            insertNotification(owner, "Notif leída", NotificationEntityType.SOLUTION, true);
            insertNotification(owner, "Notif no leída", NotificationEntityType.SOLUTION, false);
            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].read", is(false)))
                    .andExpect(jsonPath("$.content[1].read", is(true)));
        }

        @Test
        @DisplayName("Debe respetar el tamaño de página y señalar hasNext correctamente")
        void getNotifications_MoreThanPageSize_ReturnsHasNextTrue() throws Exception {
            // El token es de TEACHER, que solo ve SOLUTION
            for (int i = 1; i <= 4; i++) {
                insertNotification(owner, "Notificación " + i, NotificationEntityType.SOLUTION, false);
            }
            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "0")
                            .param("size", "2")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.hasNext", is(true)));

            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "1")
                            .param("size", "2")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.hasNext", is(false)));
        }

        @Test
        @DisplayName("Un TEACHER solo debe ver notificaciones de tipo SOLUTION")
        void getNotifications_Teacher_SeesOnlySolution() throws Exception {
            insertNotification(owner, "Nueva solución", NotificationEntityType.SOLUTION, false);
            insertNotification(owner, "Nueva tarea", NotificationEntityType.TASK, false);
            insertNotification(owner, "Nueva corrección", NotificationEntityType.CORRECTION, false);
            insertNotification(owner, "Nuevo tema", NotificationEntityType.TOPIC, false);
            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].entityType", is("SOLUTION")));
        }

        @Test
        @DisplayName("Un STUDENT solo debe ver notificaciones de tipo TASK, CORRECTION y TOPIC")
        void getNotifications_Student_DoesNotSeeSolution() throws Exception {
            String studentToken = generateToken(owner, Role.STUDENT);

            insertNotification(owner, "Nueva solución", NotificationEntityType.SOLUTION, false);
            insertNotification(owner, "Nueva tarea", NotificationEntityType.TASK, false);
            insertNotification(owner, "Nueva corrección", NotificationEntityType.CORRECTION, false);
            insertNotification(owner, "Nuevo tema", NotificationEntityType.TOPIC, false);
            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(get("/api/v1/notifications/owner/{ownerId}", owner.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)));
        }
    }

    // ─── GET /api/v1/notifications/mark-as-read/{id} ─────────────────────────

    @Nested
    @DisplayName("GET /api/v1/notifications/mark-as-read/{id}")
    class MarkAsReadTests {

        @Test
        @DisplayName("Debe marcar la notificación como leída y devolver 200")
        void markAsRead_ExistingNotification_Returns200() throws Exception {
            NotificationJpaEntity notif = insertNotification(owner, "Nueva tarea disponible", NotificationEntityType.SOLUTION, false);
            entityManager.flush();

            mockMvc.perform(get("/api/v1/notifications/mark-as-read/{id}", notif.getId())
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk());

            // Verificamos que el cambio se persiste en BD
            entityManager.flush();
            entityManager.clear();
            NotificationJpaEntity updated = entityManager.find(NotificationJpaEntity.class, notif.getId());
            org.junit.jupiter.api.Assertions.assertTrue(updated.isRead());
        }

        @Test
        @DisplayName("Debe devolver 400 si la notificación no existe")
        void markAsRead_NonExistentId_Returns400() throws Exception {
            mockMvc.perform(get("/api/v1/notifications/mark-as-read/{id}", 99999L)
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Inserts a notification directly into the database and returns the persisted entity.
     *
     * @param owner      Notification owner.
     * @param message    Notification message.
     * @param entityType The entity type of the notification.
     * @param read       Whether the notification is already read.
     * @return The persisted entity.
     */
    private NotificationJpaEntity insertNotification(UserJpaEntity owner, String message, NotificationEntityType entityType, boolean read) {
        NotificationJpaEntity notif = NotificationJpaEntity.builder()
                .owner(owner)
                .message(message)
                .entityType(entityType)
                .entityId(1L)
                .read(read)
                .build();
        entityManager.persist(notif);
        return notif;
    }

    /**
     * Genera un token JWT para el usuario y rol dados.
     *
     * @param userEntity La entidad JPA del usuario.
     * @param role       El rol para el que se genera el token.
     * @return El token JWT como String.
     */
    private String generateToken(UserJpaEntity userEntity, Role role) {
        User user = User.create(
                new UserId(userEntity.getId()),
                new Email(userEntity.getEmail()),
                Password.hashed(userEntity.getPassword()),
                new Name(userEntity.getFirstName()),
                new Name(userEntity.getLastName()),
                userEntity.isAdmin()
        );
        return jwtService.generateToken(user, role);
    }
}
