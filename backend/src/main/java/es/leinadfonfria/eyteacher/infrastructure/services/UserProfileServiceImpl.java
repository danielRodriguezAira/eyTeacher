package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.*;
import es.leinadfonfria.eyteacher.application.services.auth.LoginUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.RegisterUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.UpdatePasswordUseCase;
import es.leinadfonfria.eyteacher.application.services.auth.UpdateUserProfileUseCase;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.UserRepository;
import es.leinadfonfria.eyteacher.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static es.leinadfonfria.eyteacher.domain.errors.ErrorCode.INVALID_CREDENTIALS;
import static es.leinadfonfria.eyteacher.domain.errors.ErrorCode.UNKNOWN_ERROR;

/**
 * Implementation of user-related use cases.
 * Orchestrates the login and registration processes using repositories, mappers, and security services.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements LoginUseCase, RegisterUseCase, UpdateUserProfileUseCase, UpdatePasswordUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticates a user and generates a session response.
     *
     * @param request The login credentials.
     * @return AuthUserResponse The session details and token.
     * @throws RuntimeException If the credentials are invalid.
     */
    @Override
    @Transactional(readOnly = true)
    public Result<AuthUserResponse, Integer> login(LoginRequest request) {
        try {
            UserJpaEntity entity = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new AuthException("Invalid credentials", INVALID_CREDENTIALS));

            if (!passwordEncoder.matches(request.password(), entity.getPassword())) {
                throw new AuthException("Invalid credentials", INVALID_CREDENTIALS);
            }

            User domainUser = userMapper.toDomain(entity);
            Role selectedRole = request.role() != null ? request.role() : Role.STUDENT;
            String token = jwtService.generateToken(domainUser, selectedRole);

            return Result.ok(new AuthUserResponse(
                    token,
                    domainUser.isAdmin(),
                    domainUser.getEmail().value(),
                    domainUser.getId().value().toString(),
                    Instant.now().plus(24, ChronoUnit.HOURS),
                    domainUser.getFirstName().value(),
                    domainUser.getLastName().value(),
                    selectedRole
            ));
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param request The registration details.
     * @return UserId The identifier of the newly created user.
     * @throws AuthException If the email is already in use.
     */
    @Override
    @Transactional
    public Result<Void, Integer> register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.email())) {
                throw new AuthException("Email already in use", ErrorCode.EMAIL_ALREADY_IN_USE);
            }

            Password.raw(request.password());

            User user = User.create(
                    UserId.generate(),
                    new Email(request.email()),
                    Password.hashed(passwordEncoder.encode(request.password())),
                    new Name(request.firstName()),
                    new Name(request.lastName()),
                    false
            );

            UserJpaEntity entity = userMapper.toEntity(user);
            userRepository.save(entity);

            return Result.ok(null);
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }

    /**
     * Updates an existing user by changing email, firstName and lastName.
     * Password and admin flag are not modified here.
     *
     * @param request The update details.
     * @return UserId The identifier of the updated user.
     */
    @Override
    @Transactional
    public Result<Void, Integer> update(UpdateUserProfileRequest request) {
        try {
            UUID userUuid;
            try {
                userUuid = UUID.fromString(request.id());
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid user ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }

            User userUpdated;
            try {
                userUpdated = User.update(
                        new UserId(userUuid),
                        userMapper.toEmail(request.email()),
                        userMapper.toName(request.firstName()),
                        userMapper.toName(request.lastName()));
            } catch (IllegalArgumentException e) {
                throw new AuthException("Error updating user profile", e, ErrorCode.INVALID_USER_DATA);
            }

            UserJpaEntity entity = userRepository.findById(userUpdated.getId().value())
                    .orElseThrow(() -> new AuthException("User not found", ErrorCode.USER_NOT_FOUND));

            // If email is changing, ensure uniqueness
            String newEmail = userUpdated.getEmail().value();
            if (!newEmail.equals(entity.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new AuthException("Email already in use", ErrorCode.EMAIL_ALREADY_IN_USE);
                }
                entity.setEmail(newEmail);
            }

            entity.setFirstName(userUpdated.getFirstName().value());
            entity.setLastName(userUpdated.getLastName().value());

            userRepository.save(entity);
            return Result.ok(null);
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during profile update", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }

    /**
     * Updates the user's password.
     * Verifies that the current password is correct and that the new password is different from the current one.
     *
     * @param request The update password details.
     * @throws AuthException If the user is not found, the current password is incorrect, or the new password is the same as the current one.
     */
    @Override
    @Transactional
    public Result<Void, Integer> updatePassword(UpdatePasswordRequest request) {
        try {
            UUID userUuid = UUID.fromString(request.id());
            UserJpaEntity entity = userRepository.findById(userUuid)
                    .orElseThrow(() -> new AuthException("User not found", ErrorCode.USER_NOT_FOUND));

            if(!passwordEncoder.matches(request.oldPassword(), entity.getPassword())) {
                throw new AuthException("Invalid current password", ErrorCode.INVALID_PASSWORD);
            }

            if (passwordEncoder.matches(request.newPassword(), entity.getPassword())) {
                throw new AuthException("New password must be different from the current one", ErrorCode.DIFFERENT_PASSWORD);
            }

            Password.raw(request.newPassword());

            entity.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(entity);
            return Result.ok(null);
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }

}
