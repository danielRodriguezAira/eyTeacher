package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TaskJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.SolutionMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TaskMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TopicMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.SolutionJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TaskJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepository<Task> {

    private final TaskJpaRepository taskJpaRepository;
    private final TopicJpaRepository topicJpaRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final TaskMapper taskMapper;
    private final TopicMapper topicMapper;
    private final SolutionMapper solutionMapper;
    private final UserMapper userMapper;

    @Override
    public Task findById(Long id) {
        TaskJpaEntity taskEntity = taskJpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND));
        Task task = taskMapper.toDomain(taskEntity);
        List<User> studentList = userMapper.toDomainList(taskEntity.getTopic().getStudentList());
        Topic topic = topicMapper.toDomain(taskEntity.getTopic(), studentList);
        return Task.create(task.getId(), task.getDescription(), topic, task.getCreatedAt());
    }

    @Override
    public PageResult<Task> findByTopicId(Long topicId, int page, int size) {
        topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));
        List<TaskJpaEntity> raw = taskJpaRepository.findPageByTopicId(topicId, page * size, size + 1);
        boolean hasNext = raw.size() > size;
        List<Task> content = taskMapper.toDomainList(hasNext ? raw.subList(0, size) : raw);
        return new PageResult<>(content, hasNext);
    }

    @Override
    public Task save(Task task) {
        TopicJpaEntity topicEntity = topicJpaRepository.findById(task.getTopic().getId())
                .orElseThrow(() -> new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));
        TaskJpaEntity taskJpaEntity = taskMapper.toEntity(task);
        taskJpaEntity.setTopic(topicEntity);
        return taskMapper.toDomain(taskJpaRepository.save(taskJpaEntity));
    }

    @Override
    public Task update(Task task) {
        TaskJpaEntity existing = taskJpaRepository.findById(task.getId())
                .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND));
        existing.setDescription(task.getDescription());
        return taskMapper.toDomain(
                taskJpaRepository.save(existing));
    }

    @Override
    public List<Task> findByStudentId(UUID studentId) {
        return mapTaskEntitiesForStudent(taskJpaRepository.findByStudentId(studentId), studentId);
    }

    @Override
    public List<Task> findByStudentIdAndTeacherId(UUID studentId, UUID teacherId) {
        return mapTaskEntitiesForStudent(taskJpaRepository.findByStudentIdAndTeacherId(studentId, teacherId), studentId);
    }

    private List<Task> mapTaskEntitiesForStudent(List<TaskJpaEntity> taskEntities, UUID studentId) {
        return taskEntities.stream()
                .map(taskEntity -> {
                    Task task = taskMapper.toDomain(taskEntity);
                    List<User> studentList = userMapper.toDomainList(taskEntity.getTopic().getStudentList());
                    Topic topic = topicMapper.toDomain(taskEntity.getTopic(), studentList);
                    List<Solution> solutionList = solutionMapper.toDomainList(
                            solutionJpaRepository.findByTaskIdAndStudentId(taskEntity.getId(), studentId));
                    return Task.create(task.getId(), task.getDescription(), topic, solutionList, task.getCreatedAt());
                })
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return taskJpaRepository.existsById(id);
    }

    /**
     * Checks whether any task exists for the given topic.
     *
     * @param topicId The topic identifier.
     * @return {@code true} if at least one task belongs to the topic, {@code false} otherwise.
     */
    @Override
    public boolean existsByTopicId(Long topicId) {
        return taskJpaRepository.existsByTopicId(topicId);
    }

    @Override
    public void delete(Long id) {
        taskJpaRepository.deleteById(id);
    }
}
