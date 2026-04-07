package es.leinadfonfria.eyteacher.application.shared;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ValueObjectMapper {

    @Named("toName")
    default Name toName(String name) {
        return new Name(name);
    }

    @Named("fromName")
    default String fromName(Name name) {
        return name.value();
    }

    @Named("toEmail")
    default Email toEmail(String value) {
        return new Email(value);
    }

    @Named("fromEmail")
    default String fromEmail(Email email) {
        return email.value();
    }

    @Named("toUserId")
    default UserId toUserId(UUID value) {
        return new UserId(value);
    }

    @Named("fromUserId")
    default UUID fromUserId(UserId userId) {
        return userId.value();
    }
}
