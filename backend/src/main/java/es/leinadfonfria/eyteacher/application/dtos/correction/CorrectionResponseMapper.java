package es.leinadfonfria.eyteacher.application.dtos.correction;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.Correction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, ValueObjectMapper.class})
public interface CorrectionResponseMapper {
    @Mapping(target = "teacher.id", source = "teacher.id", qualifiedByName = "userIdToString")
    @Mapping(target = "teacher.email", source = "teacher.email", qualifiedByName = "fromEmail")
    @Mapping(target = "teacher.firstName", source = "teacher.firstName", qualifiedByName = "fromName")
    @Mapping(target = "teacher.lastName", source = "teacher.lastName", qualifiedByName = "fromName")
    CorrectionResponse toCorrectionResponse(Correction correction);
}
