package es.leinadfonfria.eyteacher.application.dtos.correction;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Correction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface CorrectionResponseMapper {
    @Mapping(target = "teacher.id", source = "teacher.id", qualifiedByName = "userIdToString")
    @Mapping(target = "teacher.email", source = "teacher.email", qualifiedByName = "emailToString")
    @Mapping(target = "teacher.firstName", source = "teacher.firstName", qualifiedByName = "nameToString")
    @Mapping(target = "teacher.lastName", source = "teacher.lastName", qualifiedByName = "nameToString")
    CorrectionResponse toCorrectionResponse(Correction correction);
}
