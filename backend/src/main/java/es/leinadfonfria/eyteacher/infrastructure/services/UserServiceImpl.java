package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.AuthUserResponse;
import es.leinadfonfria.eyteacher.application.dtos.LoginRequest;
import es.leinadfonfria.eyteacher.application.dtos.RegisterRequest;
import es.leinadfonfria.eyteacher.application.services.LoginUseCase;
import es.leinadfonfria.eyteacher.application.services.RegisterUseCase;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.UserException;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
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

/**
 * Implementation of user-related use cases.
 * Orchestrates the login and registration processes using repositories, mappers, and security services.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements LoginUseCase, RegisterUseCase {

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
    public AuthUserResponse login(LoginRequest request) {
        UserJpaEntity entity = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), entity.getPassword())) {
            throw new UserException("Invalid credentials");
        }

        User domainUser = userMapper.toDomain(entity);
        Role selectedRole = request.role() != null ? request.role() : Role.STUDENT;
        String token = jwtService.generateToken(domainUser, selectedRole);

        return new AuthUserResponse(
                token,
                domainUser.isAdmin(),
                domainUser.getEmail().value(),
                domainUser.getId().value().toString(),
                Instant.now().plus(24, ChronoUnit.HOURS),
                domainUser.getFirstName().value(),
                domainUser.getLastName().value(),
                selectedRole
        );
    }

    /**
     * Registers a new user in the system.
     *
     * @param request The registration details.
     * @return UserId The identifier of the newly created user.
     * @throws UserException If the email is already in use.
     */
    @Override
    @Transactional
    public UserId register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserException("Email already in use");
        }

        User user = User.create(
                UserId.generate(),
                new Email(request.email()),
                passwordEncoder.encode(request.password()),
                new Name(request.firstName()),
                new Name(request.lastName()),
                request.isAdmin() != null && request.isAdmin()
        );

        UserJpaEntity entity = userMapper.toEntity(user);
        userRepository.save(entity);

        return user.getId();
    }
}
