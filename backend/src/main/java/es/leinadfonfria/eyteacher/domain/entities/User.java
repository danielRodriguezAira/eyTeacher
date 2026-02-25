package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a User in the system.
 * This is a domain entity that holds core user information and business rules.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private final UserId id;
    private final Email email;
    private final Password password; // hashed VO
    private final Name firstName;
    private final Name lastName;
    private final boolean isAdmin;

    /**
     * Creates a new User instance.
     * Used to instantiate the domain entity with all its required attributes.
     *
     * @param id        The unique identifier of the user.
     * @param email     The user's email address.
     * @param password  The user's hashed password.
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param isAdmin   Flag indicating if the user has administrator privileges.
     * @return User A new user domain entity.
     */
    public static User create(UserId id, Email email, Password password, Name firstName, Name lastName, boolean isAdmin) {
        return new User(id, email, password, firstName, lastName, isAdmin);
    }

    public static User update(UserId userId, Email email, Name firstName, Name lastName) {
        return User.builder()
                .id(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
