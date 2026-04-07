package es.leinadfonfria.eyteacher.application.dtos.auth;

import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ValueObjectMapper.class})
public interface UserResponseMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "userIdToString")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "fromName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "fromName")
    @Mapping(target = "email", source = "email", qualifiedByName = "fromEmail")
    UserResponse toStudentResponse(User user);

    List<UserResponse> toStudentResponseList(List<User> users);

    @Named("userIdToString")
    default String userIdToString(UserId userId) {
        return userId.value().toString();
    }

}
