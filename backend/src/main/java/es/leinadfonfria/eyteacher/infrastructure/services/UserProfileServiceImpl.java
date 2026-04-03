package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.*;
import es.leinadfonfria.eyteacher.application.services.auth.*;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
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
public class UserProfileServiceImpl implements LoginUseCase, RegisterUseCase, UpdateUserProfileUseCase, UpdatePasswordUseCase, GetStudentsByOwnerIdUseCase {

    private final UserRepository<User> userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserResponseMapper userResponseMapper;

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
            User domainUser = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new AuthException("Invalid credentials", INVALID_CREDENTIALS));

            if (!passwordEncoder.matches(request.password(), domainUser.getPassword().value())) {
                throw new AuthException("Invalid credentials", INVALID_CREDENTIALS);
            }

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
     * @return Void
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

            userRepository.save(user);
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
     * @return Void
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

            User existing = userRepository.findById(userUpdated.getId().value())
                    .orElseThrow(() -> new AuthException("User not found", ErrorCode.USER_NOT_FOUND));

            String newEmail = userUpdated.getEmail().value();
            if (!newEmail.equals(existing.getEmail().value())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new AuthException("Email already in use", ErrorCode.EMAIL_ALREADY_IN_USE);
                }
            }

            User updated = User.create(
                    userUpdated.getId(),
                    userUpdated.getEmail(),
                    existing.getPassword(),
                    userUpdated.getFirstName(),
                    userUpdated.getLastName(),
                    existing.isAdmin()
            );

            userRepository.save(updated);
            return Result.ok(null);
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during profile update", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves a page of distinct students enrolled in any topic whose category is owned by the given teacher.
     *
     * @param ownerId The string representation of the teacher's UUID.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of {@link UserResponse} students, or an error code on failure.
     */
    @Override
    @Transactional(readOnly = true)
    public Result<PageResponse<UserResponse>, Integer> getStudentsByOwnerId(String ownerId, int page, int size) {
        try {
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(ownerId);
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid owner ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }

            var pageResult = userRepository.findStudentsByOwnerId(ownerUuid, page, size);
            return Result.ok(new PageResponse<>(userResponseMapper.toStudentResponseList(pageResult.content()), page, size, pageResult.hasNext()));
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error retrieving students by owner id", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
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
            User existing = userRepository.findById(userUuid)
                    .orElseThrow(() -> new AuthException("User not found", ErrorCode.USER_NOT_FOUND));

            if (!passwordEncoder.matches(request.oldPassword(), existing.getPassword().value())) {
                throw new AuthException("Invalid current password", ErrorCode.INVALID_PASSWORD);
            }

            if (passwordEncoder.matches(request.newPassword(), existing.getPassword().value())) {
                throw new AuthException("New password must be different from the current one", ErrorCode.DIFFERENT_PASSWORD);
            }

            Password.raw(request.newPassword());

            User updated = User.create(
                    existing.getId(),
                    existing.getEmail(),
                    Password.hashed(passwordEncoder.encode(request.newPassword())),
                    existing.getFirstName(),
                    existing.getLastName(),
                    existing.isAdmin()
            );

            userRepository.save(updated);
            return Result.ok(null);
        } catch (AuthException e) {
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            return Result.fail(UNKNOWN_ERROR);
        }
    }
}
