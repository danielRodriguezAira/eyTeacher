package es.leinadfonfria.eyteacher.application.dtos.solution;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface SolutionResponseMapper {

    @Mapping(target = "task.solutionList", ignore = true)
    @Mapping(target = "student.email", source = "student.email", qualifiedByName = "emailToString")
    @Mapping(target = "student.firstName", source = "student.firstName", qualifiedByName = "nameToString")
    @Mapping(target = "student.lastName", source = "student.lastName", qualifiedByName = "nameToString")
    @Mapping(target = "student.id", source = "student.id", qualifiedByName = "userIdToString")
    SolutionResponse toSolutionResponse(Solution solution);
}
