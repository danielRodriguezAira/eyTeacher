package es.leinadfonfria.eyteacher.infrastructure.security;

import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service for JWT operations.
 * Handles generation, validation, and extraction of information from JWT tokens.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Generates a JWT token for the given user and selected role.
     * Includes user identity and claims such as email and admin status.
     *
     * @param user The user for whom the token is generated.
     * @param role The role selected for the session.
     * @return String The generated JWT token.
     */
    public String generateToken(User user, Role role) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        Instant expiration = now.plus(24, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(user.getId().value().toString())
                .claim("email", user.getEmail().value())
                .claim("name", user.getFirstName().value() + " " + user.getLastName().value())
                .claim("admin", user.isAdmin())
                .claim("roles", List.of("ROLE_" + role.name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Validates a JWT token and returns its claims.
     *
     * @param token The token to validate.
     * @return Claims The payload of the token.
     * @throws io.jsonwebtoken.JwtException If the token is invalid.
     */
    public Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID from the token's subject.
     *
     * @param token The JWT token.
     * @return UUID The user identifier.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Checks if a token has expired.
     *
     * @param token The JWT token.
     * @return boolean True if the token is expired.
     */
    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration().before(new Date());
    }
}
