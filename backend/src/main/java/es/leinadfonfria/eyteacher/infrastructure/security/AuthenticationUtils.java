package es.leinadfonfria.eyteacher.infrastructure.security;

import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthenticationUtils {

    public static boolean isTeacher() {
        try {
            return getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), Role.TEACHER.getRoleName()));
        } catch (Exception ex) {
            throw new AuthException("Failed to retrieve authentication", ErrorCode.AUTHENTICATION_ERROR);
        }
    }

    public static boolean isStudent() {
        try {
            return getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), Role.STUDENT.getRoleName()));
        } catch (Exception ex) {
            throw new AuthException("Failed to retrieve authentication", ErrorCode.AUTHENTICATION_ERROR);
        }
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
