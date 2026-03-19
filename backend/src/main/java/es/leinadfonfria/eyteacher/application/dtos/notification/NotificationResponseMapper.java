package es.leinadfonfria.eyteacher.application.dtos.notification;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface NotificationResponseMapper {
    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
