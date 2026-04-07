package es.leinadfonfria.eyteacher.application.dtos.solution;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.correction.CorrectionResponseMapper;
import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, CorrectionResponseMapper.class, ValueObjectMapper.class})
public interface SolutionResponseMapper {

    @Mapping(target = "task.solutionList", ignore = true)
    @Mapping(target = "task.topicId", ignore = true)
    @Mapping(target = "correction.teacher.id", source = "correction.teacher.id", qualifiedByName = "userIdToString")
    @Mapping(target = "correction.teacher.email", source = "correction.teacher.email", qualifiedByName = "fromEmail")
    @Mapping(target = "correction.teacher.firstName", source = "correction.teacher.firstName", qualifiedByName = "fromName")
    @Mapping(target = "correction.teacher.lastName", source = "correction.teacher.lastName", qualifiedByName = "fromName")
    @Mapping(target = "student.email", source = "student.email", qualifiedByName = "fromEmail")
    @Mapping(target = "student.firstName", source = "student.firstName", qualifiedByName = "fromName")
    @Mapping(target = "student.lastName", source = "student.lastName", qualifiedByName = "fromName")
    @Mapping(target = "student.id", source = "student.id", qualifiedByName = "userIdToString")
    SolutionResponse toSolutionResponse(Solution solution);
}
