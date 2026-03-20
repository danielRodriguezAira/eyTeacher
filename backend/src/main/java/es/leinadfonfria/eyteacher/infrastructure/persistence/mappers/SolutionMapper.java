package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CorrectionMapper.class})
public interface SolutionMapper {

    @Mapping(target = "task.topic", ignore = true)
    @Mapping(target = "task.solutionList", ignore = true)
    @Mapping(target = "correction.solutionId", source = "correction.solution.id")
    Solution toDomain(SolutionJpaEntity entity);

    List<Solution> toDomainList(List<SolutionJpaEntity> entityList);

    @Mapping(target = "task", ignore = true)
    @Mapping(target = "correction", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SolutionJpaEntity toEntity(Solution domain);
}
