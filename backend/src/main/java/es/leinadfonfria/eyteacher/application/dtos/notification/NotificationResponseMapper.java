package es.leinadfonfria.eyteacher.application.dtos.notification;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface NotificationResponseMapper {
    @Mapping(target = "createdAt", ignore = true)
    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
