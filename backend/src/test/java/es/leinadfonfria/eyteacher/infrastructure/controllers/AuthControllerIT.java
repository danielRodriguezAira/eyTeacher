package es.leinadfonfria.eyteacher.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.leinadfonfria.eyteacher.config.TestRabbitConfig;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AuthController}.
 *
 * <h2>¿Qué es un test de integración?</h2>
 * Un test unitario aísla una clase sustituyendo todas sus dependencias por mocks.
 * Un test de integración, en cambio, arranca el contexto real de Spring (con JPA,
 * seguridad, Jackson, etc.) y ejercita varios componentes trabajando juntos.
 * El resultado es mayor confianza: si pasa, el endpoint funcionará en producción.
 *
 * <h2>Anotaciones clave</h2>
 * <ul>
 *   <li>{@code @SpringBootTest} — arranca el contexto completo de Spring Boot
 *       (equivale a iniciar la aplicación, pero sin servidor HTTP real).</li>
 *   <li>{@code @AutoConfigureMockMvc} — crea un {@link MockMvc} que simula
 *       peticiones HTTP sin levantar un puerto de red.</li>
 *   <li>{@code @ActiveProfiles("test")} — activa {@code application-test.properties},
 *       que sustituye MySQL por una base de datos H2 en memoria.</li>
 *   <li>{@code @Transactional} — cada test se ejecuta dentro de una transacción
 *       que se hace rollback al finalizar: los datos no se acumulan entre tests.</li>
 *   <li>{@code @Import(TestRabbitConfig.class)} — reemplaza la conexión real a
 *       RabbitMQ por un mock para que los tests no necesiten un broker arrancado.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestRabbitConfig.class)
class AuthControllerIT {

    /** MockMvc simula el ciclo completo de una petición HTTP: filtros de seguridad,
     *  deserialización, controlador, serialización de respuesta. */
    @Autowired
    private MockMvc mockMvc;

    /** ObjectMapper convierte objetos Java a JSON y viceversa en los tests. */
    @Autowired
    private ObjectMapper objectMapper;

    /** EntityManager permite insertar datos directamente en la base de datos H2
     *  de prueba sin pasar por la capa de servicio. */
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ─── Registro ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Debe registrar un usuario nuevo y devolver 200")
        void register_NewUser_Returns200() throws Exception {
            // perform() lanza la petición HTTP simulada.
            // contentType + content definen el cuerpo JSON.
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", "nuevo@example.com",
                                    "password", "Password1!",
                                    "firstName", "Ana",
                                    "lastName", "García"
                            ))))
                    // andExpect encadena aserciones sobre la respuesta.
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debe devolver 400 si el email ya está registrado")
        void register_DuplicateEmail_Returns400() throws Exception {
            // Insertamos un usuario con ese email antes de la llamada
            persistTeacher("duplicado@example.com");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", "duplicado@example.com",
                                    "password", "Password1!",
                                    "firstName", "Ana",
                                    "lastName", "García"
                            ))))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Debe devolver 200 con token JWT al autenticarse correctamente")
        void login_ValidCredentials_ReturnsToken() throws Exception {
            // Creamos el usuario en BD con la contraseña hasheada
            persistTeacherWithPassword("teacher@example.com", "Password1!");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", "teacher@example.com",
                                    "password", "Password1!",
                                    "role", Role.TEACHER.name()
                            ))))
                    .andExpect(status().isOk())
                    // jsonPath navega el JSON de respuesta con expresiones tipo XPath
                    .andExpect(jsonPath("$.token", not(emptyString())));
        }

        @Test
        @DisplayName("Debe devolver 401 con contraseña incorrecta")
        void login_WrongPassword_Returns401() throws Exception {
            persistTeacherWithPassword("teacher@example.com", "Password1!");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", "teacher@example.com",
                                    "password", "WrongPass1!",
                                    "role", Role.TEACHER.name()
                            ))))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Debe devolver 401 si el usuario no existe")
        void login_UnknownUser_Returns401() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", "noexiste@example.com",
                                    "password", "Password1!",
                                    "role", Role.TEACHER.name()
                            ))))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Persiste un profesor con contraseña fija hasheada para usarlo en tests de login.
     *
     * @param email    Email del profesor.
     * @param rawPassword Contraseña en texto plano (se hashea con BCrypt).
     * @return La entidad JPA persistida.
     */
    private UserJpaEntity persistTeacherWithPassword(String email, String rawPassword) {
        UserJpaEntity teacher = UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName("Test")
                .lastName("Teacher")
                .isAdmin(false)
                .build();
        entityManager.persist(teacher);
        entityManager.flush();
        return teacher;
    }

    /**
     * Persiste un profesor con contraseña aleatoria (para tests que no necesitan hacer login).
     *
     * @param email Email del profesor.
     * @return La entidad JPA persistida.
     */
    private UserJpaEntity persistTeacher(String email) {
        return persistTeacherWithPassword(email, "Password1!");
    }
}
