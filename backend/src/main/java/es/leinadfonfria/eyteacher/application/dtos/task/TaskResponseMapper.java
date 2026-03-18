package es.leinadfonfria.eyteacher.application.dtos.task;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SolutionResponseMapper.class})
public interface TaskResponseMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "solutionList", source = "solutionList")
    TaskResponse toTaskResponse(Task task);
}
