package es.leinadfonfria.eyteacher.application.dtos.auth;

import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "userIdToString")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "nameToString")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "nameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    UserResponse toStudentResponse(User user);

    List<UserResponse> toStudentResponseList(List<User> users);

    @Named("userIdToString")
    default String userIdToString(UserId userId) {
        return userId.value().toString();
    }

    @Named("nameToString")
    default String nameToString(Name name) {
        return name.value();
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email.value();
    }
}
