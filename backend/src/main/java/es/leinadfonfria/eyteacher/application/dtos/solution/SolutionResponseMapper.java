package es.leinadfonfria.eyteacher.application.dtos.solution;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.correction.CorrectionResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, CorrectionResponseMapper.class})
public interface SolutionResponseMapper {

    @Mapping(target = "task.solutionList", ignore = true)
    @Mapping(target = "correction.teacher.id", source = "correction.teacher.id", qualifiedByName = "userIdToString")
    @Mapping(target = "correction.teacher.email", source = "correction.teacher.email", qualifiedByName = "emailToString")
    @Mapping(target = "correction.teacher.firstName", source = "correction.teacher.firstName", qualifiedByName = "nameToString")
    @Mapping(target = "correction.teacher.lastName", source = "correction.teacher.lastName", qualifiedByName = "nameToString")
    @Mapping(target = "student.email", source = "student.email", qualifiedByName = "emailToString")
    @Mapping(target = "student.firstName", source = "student.firstName", qualifiedByName = "nameToString")
    @Mapping(target = "student.lastName", source = "student.lastName", qualifiedByName = "nameToString")
    @Mapping(target = "student.id", source = "student.id", qualifiedByName = "userIdToString")
    SolutionResponse toSolutionResponse(Solution solution);
}
