package es.leinadfonfria.eyteacher.application.dtos.topic;

import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SaveTopicRequestMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "toTopicName")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    @Mapping(target = "taskList", ignore = true)
    Topic toDomain(SaveTopicRequest request);

    @Named("toTopicName")
    default Name toTopicName(String name) {
        return new Name(name);
    }
}
