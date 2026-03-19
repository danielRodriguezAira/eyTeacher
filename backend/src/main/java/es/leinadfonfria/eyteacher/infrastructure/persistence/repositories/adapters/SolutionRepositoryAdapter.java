package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.SolutionRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.SolutionJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.SolutionMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.SolutionJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TaskJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SolutionRepositoryAdapter implements SolutionRepository<Solution> {

    private final SolutionJpaRepository solutionJpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final SolutionMapper solutionMapper;

    @Override
    public Optional<Solution> findById(Long id) {
        return solutionJpaRepository.findById(id)
                .map(solutionMapper::toDomain);
    }

    @Override
    public List<Solution> findByTaskId(Long taskId) {
        TaskJpaEntity taskEntity = taskJpaRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND));
        return solutionMapper.toDomainList(solutionJpaRepository.findByTask(taskEntity));
    }

    @Override
    public Solution save(Solution solution, Long taskId) {
        TaskJpaEntity taskEntity = taskJpaRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND));
        SolutionJpaEntity solutionJpaEntity = solutionMapper.toEntity(solution);
        solutionJpaEntity.setTask(taskEntity);
        return solutionMapper.toDomain(solutionJpaRepository.save(solutionJpaEntity));
    }

    public List<Solution> findByTaskIdAndStudentId(Long taskId, UUID studentId) {
        TaskJpaEntity task = taskJpaRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND));
        List<SolutionJpaEntity> solutionList = solutionJpaRepository.findByTaskAndStudentId(task, studentId);
        return solutionMapper.toDomainList(solutionList);
    }
}
