package es.leinadfonfria.eyteacher.application.dtos.topic;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponseMapper;
import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, TaskResponseMapper.class, ValueObjectMapper.class})
public interface TopicResponseMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "fromName")
    @Mapping(target = "categoryId", source = "category.id")
    TopicResponse toTopicResponse(Topic topic);

    List<TopicResponse> toTopicResponseList(List<Topic> topics);
}
