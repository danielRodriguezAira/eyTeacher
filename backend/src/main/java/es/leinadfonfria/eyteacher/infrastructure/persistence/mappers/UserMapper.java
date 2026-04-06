package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper for converting between User domain entities and JPA entities.
 * Uses MapStruct to automate the mapping of value objects and standard fields.
 */
@Mapper(componentModel = "spring", uses = {ValueObjectMapper.class})
public interface UserMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity The persistence entity.
     * @return User The domain entity.
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "toUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "toEmail")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "toName")
    @Mapping(target = "password", source = "password", qualifiedByName = "toPassword")
    @Mapping(target = "isAdmin", ignore = true)
    User toDomain(UserJpaEntity entity);

    List<User> toDomainList(List<UserJpaEntity> entityList);

    /**
     * Converts a domain entity to a JPA entity.
     *
     * @param domain The domain entity.
     * @return UserJpaEntity The persistence entity.
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "fromUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "fromEmail")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "fromName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "fromName")
    @Mapping(target = "password", source = "password", qualifiedByName = "fromPassword")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isAdmin", ignore = true)
    UserJpaEntity toEntity(User domain);

    @Named("toPassword")
    default Password toPassword(String value) {
        return Password.hashed(value);
    }

    @Named("fromPassword")
    default String fromPassword(Password password) {
        return password.value();
    }

    List<UserJpaEntity> toEntityList(List<User> userList);
}
