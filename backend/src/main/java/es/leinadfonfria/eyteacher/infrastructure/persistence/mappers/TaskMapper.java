package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TopicMapper.class, SolutionMapper.class})
public interface TaskMapper {

    @Mapping(target = "topic.name", ignore = true)
    @Mapping(target = "topic.description", ignore = true)
    @Mapping(target = "topic.category", ignore = true)
    @Mapping(target = "topic.studentList", ignore = true)
    @Mapping(target = "topic.taskList", ignore = true)
    @Mapping(target = "topic.id", source = "topic.id")
    Task toDomain(TaskJpaEntity entity);

    List<Task> toDomainList(List<TaskJpaEntity> entityList);

    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskJpaEntity toEntity(Task domain);
}
