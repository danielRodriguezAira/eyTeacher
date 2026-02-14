package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.LoginRequest;
import es.leinadfonfria.eyteacher.application.dtos.RegisterRequest;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.UserRepository;
import es.leinadfonfria.eyteacher.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserJpaEntity userJpaEntity;
    private User domainUser;
    private final String email = "test@example.com";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword123";
    private final UUID userIdUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userJpaEntity = UserJpaEntity.builder()
                .id(userIdUuid)
                .email(email)
                .password(encodedPassword)
                .firstName("John")
                .lastName("Doe")
                .isAdmin(false)
                .build();

        domainUser = User.create(
                new UserId(userIdUuid),
                new Email(email),
                encodedPassword,
                new Name("John"),
                new Name("Doe"),
                false
        );
    }

    @Nested
    @DisplayName("Tests para el método login")
    class LoginTests {

        @Test
        @DisplayName("Debe autenticar correctamente con credenciales válidas")
        void login_Success() {
            // Arrange
            LoginRequest request = new LoginRequest(email, password, Role.STUDENT);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(userJpaEntity));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            when(userMapper.toDomain(userJpaEntity)).thenReturn(domainUser);
            when(jwtService.generateToken(domainUser, Role.STUDENT)).thenReturn("fake-jwt-token");

            // Act
            AuthUserResponse response = userService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("fake-jwt-token", response.token());
            assertEquals(email, response.email());
            assertEquals(userIdUuid.toString(), response.id());
            assertEquals(Role.STUDENT, response.role());
            assertFalse(response.isAdmin());
            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(jwtService).generateToken(domainUser, Role.STUDENT);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el usuario no existe")
        void login_UserNotFound() {
            // Arrange
            LoginRequest request = new LoginRequest(email, password, null);
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(request));
            assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByEmail(email);
            verifyNoInteractions(passwordEncoder, userMapper, jwtService);
        }

        @Test
        @DisplayName("Debe lanzar excepción si la contraseña es incorrecta")
        void login_InvalidPassword() {
            // Arrange
            LoginRequest request = new LoginRequest(email, "wrongPassword", null);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(userJpaEntity));
            when(passwordEncoder.matches("wrongPassword", encodedPassword)).thenReturn(false);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(request));
            assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).matches("wrongPassword", encodedPassword);
            verifyNoInteractions(userMapper, jwtService);
        }
    }

    @Nested
    @DisplayName("Tests para el método register")
    class RegisterTests {

        @Test
        @DisplayName("Debe registrar un nuevo usuario correctamente")
        void register_Success() {
            // Arrange
            RegisterRequest request = new RegisterRequest(email, password, "John", "Doe", false);
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
            when(userMapper.toEntity(any(User.class))).thenReturn(userJpaEntity);

            // Act
            UserId resultId = userService.register(request);

            // Assert
            assertNotNull(resultId);
            verify(userRepository).existsByEmail(email);
            verify(passwordEncoder).encode(password);
            verify(userMapper).toEntity(any(User.class));
            verify(userRepository).save(userJpaEntity);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email ya está en uso")
        void register_EmailAlreadyInUse() {
            // Arrange
            RegisterRequest request = new RegisterRequest(email, password, "John", "Doe", false);
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(request));
            assertEquals("Email already in use", exception.getMessage());
            verify(userRepository).existsByEmail(email);
            verifyNoMoreInteractions(passwordEncoder, userMapper, userRepository);
        }
    }
}
