package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Notification;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface NotificationMapper {
    Notification toDomain(NotificationJpaEntity entity);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    NotificationJpaEntity toEntity(Notification domain);
}
