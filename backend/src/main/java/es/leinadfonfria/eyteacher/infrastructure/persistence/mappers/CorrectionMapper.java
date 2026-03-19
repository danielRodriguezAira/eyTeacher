package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Correction;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CorrectionJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CorrectionMapper {
    @Mapping(target = "solutionId", source = "solution.id")
    Correction toDomain(CorrectionJpaEntity entity);

    @Mapping(target = "solution", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CorrectionJpaEntity toEntity(Correction domain);
}
