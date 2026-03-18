package es.leinadfonfria.eyteacher.application.dtos.topic;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, TaskResponseMapper.class})
public interface TopicResponseMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "topicNameToString")
    @Mapping(target = "categoryId", source = "category.id")
    TopicResponse toTopicResponse(Topic topic);

    @Mapping(target = "name", source = "topic.name", qualifiedByName = "topicNameToString")
    @Mapping(target = "categoryId", source = "topic.category.id")
    @Mapping(target = "taskList", source = "taskList")
    TopicResponse toTopicResponse(Topic topic, List<Task> taskList);

    List<TopicResponse> toTopicResponseList(List<Topic> topics);

    @Named("topicNameToString")
    default String topicNameToString(Name name) {
        return name.value();
    }
}
