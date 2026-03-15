package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.auth.LoginRequest;
import es.leinadfonfria.eyteacher.application.dtos.auth.RegisterRequest;
import es.leinadfonfria.eyteacher.application.dtos.auth.UpdatePasswordRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.UserRepositoryAdapter;
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
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserProfileServiceImpl userService;

    private User domainUser;
    private final String email = "test@example.com";
    private final String password = "Password123&";
    private final String encodedPassword = "encodedPassword123&";
    private final UUID userIdUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        domainUser = User.create(
                new UserId(userIdUuid),
                new Email(email),
                Password.hashed(encodedPassword),
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
            when(userRepositoryAdapter.findByEmail(email)).thenReturn(Optional.of(domainUser));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            when(jwtService.generateToken(domainUser, Role.STUDENT)).thenReturn("fake-jwt-token");

            // Act
            Result<AuthUserResponse, Integer> response = userService.login(request);

            // Assert
            assertTrue(response.isSuccess());
            assertEquals("fake-jwt-token", response.getValue().token());
            assertEquals(email, response.getValue().email());
            assertEquals(userIdUuid.toString(), response.getValue().id());
            assertEquals(Role.STUDENT, response.getValue().role());
            assertFalse(response.getValue().isAdmin());
            verify(userRepositoryAdapter).findByEmail(email);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(jwtService).generateToken(domainUser, Role.STUDENT);
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no existe")
        void login_UserNotFound() {
            // Arrange
            LoginRequest request = new LoginRequest(email, password, null);
            when(userRepositoryAdapter.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            Result<AuthUserResponse, Integer> response = userService.login(request);
            assertTrue(response.isFailure());
            assertEquals(ErrorCode.INVALID_CREDENTIALS, response.getError());
            verify(userRepositoryAdapter).findByEmail(email);
            verifyNoInteractions(passwordEncoder, userMapper, jwtService);
        }

        @Test
        @DisplayName("Debe retornar fallo si la contraseña es incorrecta")
        void login_InvalidPassword() {
            // Arrange
            LoginRequest request = new LoginRequest(email, "wrongPassword", null);
            when(userRepositoryAdapter.findByEmail(email)).thenReturn(Optional.of(domainUser));
            when(passwordEncoder.matches("wrongPassword", encodedPassword)).thenReturn(false);

            // Act & Assert
            Result<AuthUserResponse, Integer> response = userService.login(request);
            assertTrue(response.isFailure());
            assertEquals(ErrorCode.INVALID_CREDENTIALS, response.getError());
            verify(userRepositoryAdapter).findByEmail(email);
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
            RegisterRequest request = new RegisterRequest(email, password, "John", "Doe");
            when(userRepositoryAdapter.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

            // Act
            Result<Void, Integer> result = userService.register(request);

            // Assert
            assertTrue(result.isSuccess());
            assertNull(result.getValue());
            verify(userRepositoryAdapter).existsByEmail(email);
            verify(passwordEncoder).encode(password);
            verify(userRepositoryAdapter).save(any(User.class));
        }

        @Test
        @DisplayName("Debe retornar fallo si el email ya está en uso")
        void register_EmailAlreadyInUse() {
            // Arrange
            RegisterRequest request = new RegisterRequest(email, password, "John", "Doe");
            when(userRepositoryAdapter.existsByEmail(email)).thenReturn(true);

            // Act
            Result<Void, Integer> result = userService.register(request);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.EMAIL_ALREADY_IN_USE, result.getError());
            verify(userRepositoryAdapter).existsByEmail(email);
            verifyNoMoreInteractions(passwordEncoder, userMapper, userRepositoryAdapter);
        }
    }

    @Nested
    @DisplayName("Tests para el método resetPassword")
    class ResetPasswordTests {

        @Test
        @DisplayName("Debe cambiar la contraseña correctamente")
        void resetPassword_Success() {
            // Arrange
            String newPassword = "newPassword123&";
            String encodedNewPassword = "encodedNewPassword123&";
            UpdatePasswordRequest request = new UpdatePasswordRequest(userIdUuid.toString(), password, newPassword);

            when(userRepositoryAdapter.findById(userIdUuid)).thenReturn(Optional.of(domainUser));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            when(passwordEncoder.matches(newPassword, encodedPassword)).thenReturn(false);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // Act
            Result<Void, Integer> result = userService.updatePassword(request);

            // Assert
            assertTrue(result.isSuccess());
            verify(userRepositoryAdapter).findById(userIdUuid);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(passwordEncoder).matches(newPassword, encodedPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepositoryAdapter).save(any(User.class));
        }

        @Test
        @DisplayName("Debe retornar fallo si la contraseña actual es incorrecta")
        void resetPassword_InvalidCurrentPassword() {
            // Arrange
            UpdatePasswordRequest request = new UpdatePasswordRequest(userIdUuid.toString(), "wrongCurrent", "newPass");
            when(userRepositoryAdapter.findById(userIdUuid)).thenReturn(Optional.of(domainUser));
            when(passwordEncoder.matches("wrongCurrent", encodedPassword)).thenReturn(false);

            // Act
            Result<Void, Integer> result = userService.updatePassword(request);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.INVALID_PASSWORD, result.getError());
            verify(userRepositoryAdapter).findById(userIdUuid);
            verify(passwordEncoder).matches("wrongCurrent", encodedPassword);
            verifyNoMoreInteractions(userRepositoryAdapter);
        }

        @Test
        @DisplayName("Debe retornar fallo si la nueva contraseña es igual a la actual")
        void resetPassword_SamePassword() {
            // Arrange
            UpdatePasswordRequest request = new UpdatePasswordRequest(userIdUuid.toString(), password, password);
            when(userRepositoryAdapter.findById(userIdUuid)).thenReturn(Optional.of(domainUser));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

            // Act
            Result<Void, Integer> result = userService.updatePassword(request);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.DIFFERENT_PASSWORD, result.getError());
            verify(userRepositoryAdapter).findById(userIdUuid);
            verify(passwordEncoder, times(2)).matches(password, encodedPassword);
            verifyNoMoreInteractions(userRepositoryAdapter);
        }
    }
}
