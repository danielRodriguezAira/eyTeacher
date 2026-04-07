package es.leinadfonfria.eyteacher.application.dtos.topic;

import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ValueObjectMapper.class})
public interface SaveTopicRequestMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "toName")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    @Mapping(target = "taskList", ignore = true)
    Topic toDomain(SaveTopicRequest request);
}
