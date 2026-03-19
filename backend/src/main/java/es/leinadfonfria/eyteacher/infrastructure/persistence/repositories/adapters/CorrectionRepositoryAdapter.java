package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Correction;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.CorrectionRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CorrectionJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.CorrectionMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CorrectionJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.SolutionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CorrectionRepositoryAdapter implements CorrectionRepository<Correction> {
    private final CorrectionJpaRepository correctionJpaRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final CorrectionMapper correctionMapper;

    @Override
    public Correction save(Correction correction, Long solutionId) {
        SolutionJpaEntity solutionEntity = solutionJpaRepository.findById(solutionId)
                .orElseThrow(() -> new NotFoundException("Solution not found", ErrorCode.SOLUTION_NOT_FOUND));
        CorrectionJpaEntity correctionJpaEntity = correctionMapper.toEntity(correction);
        correctionJpaEntity.setSolution(solutionEntity);
        return correctionMapper.toDomain(correctionJpaRepository.save(correctionJpaEntity));
    }

    @Override
    public Optional<Correction> findBySolutionId(Long solutionId) {
        SolutionJpaEntity solutionEntity = solutionJpaRepository.findById(solutionId)
                .orElseThrow(() -> new NotFoundException("Solution not found", ErrorCode.SOLUTION_NOT_FOUND));
        return correctionJpaRepository.findBySolution(solutionEntity)
                .map(correctionMapper::toDomain);
    }
}
