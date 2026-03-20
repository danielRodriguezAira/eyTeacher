package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponseMapper;
import es.leinadfonfria.eyteacher.application.services.task.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.ports.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.events.NewTaskEvent;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements SaveTaskUseCase, GetTaskUseCase, GetTasksByTopicUseCase, DeleteTaskUseCase {

    private final TaskRepository<Task> taskRepository;
    private final TopicRepository<Topic> topicRepository;
    private final TaskResponseMapper taskResponseMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Saves a new task or updates an existing one.
     * @param request The task save request containing task details.
     * @return Result containing the task ID or an error code.
     */
    @Override
    @Transactional
    public Result<Long, Integer> saveTask(SaveTaskRequest request) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.USER_NOT_TEACHER);
            }

            Topic topic = topicRepository.findById(request.topicId());
            validateUserHasPermission(topic);

            Task task;
            if (request.id() == null) {
                task = Task.create(request.description());
            } else if (taskRepository.existsById(request.id())) {
                task = Task.edit(request.id(), request.description());
            } else {
                throw new NotFoundException("Task not found", ErrorCode.TASK_NOT_FOUND);
            }

            Task saved = taskRepository.save(task, request.topicId());
            eventPublisher.publishEvent( new NewTaskEvent(this, saved, topic.getStudentList()));
            return Result.ok(saved.getId());
        } catch (AuthException e) {
            log.error("Authentication error during task save", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during task save", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during task save", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves a task by its ID.
     * @param id The ID of the task to retrieve.
     * @return Result containing a TaskResponse or an error code.
     */
    @Override
    public Result<TaskResponse, Integer> getTask(Long id) {
        try {
            Task task = taskRepository.findById(id);
            if (task.getTopic() != null) {
                validateUserHasPermission(task.getTopic());
            }

            return Result.ok(taskResponseMapper.toTaskResponse(task));
        } catch (AuthException e) {
            log.error("Authentication error during task retrieval", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during task retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during task retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves tasks by topic ID.
     * @param topicId The ID of the topic to retrieve tasks for.
     * @return Result containing a list of TaskResponse objects or an error code.
     */
    @Override
    public Result<List<TaskResponse>, Integer> getTasksByTopic(Long topicId) {
        try {
            Topic topic = topicRepository.findById(topicId);
            validateUserHasPermission(topic);

            List<Task> tasks = taskRepository.findByTopicId(topicId);
            return Result.ok(tasks.stream().map(taskResponseMapper::toTaskResponse).toList());
        } catch (AuthException e) {
            log.error("Authentication error during tasks retrieval", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during tasks retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during tasks retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     * @return Result indicating success or failure.
     */
    @Override
    @Transactional
    public Result<Void, Integer> deleteTask(Long id) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.USER_NOT_TEACHER);
            }
            Task task = taskRepository.findById(id);
            if (task.getTopic() != null) {
                validateUserHasPermission(task.getTopic());
            }
            taskRepository.delete(id);
            return Result.ok(null);
        } catch (AuthException e) {
            log.error("Authentication error during task deletion", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during task deletion", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during task deletion", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Validates that the authenticated user has permission to access the topic.
     * @param topic The topic domain entity to validate.
     */
    private void validateUserHasPermission(Topic topic) throws AuthException {
        UUID authUserId = AuthenticationUtils.getUserId();
        if (AuthenticationUtils.isTeacher()) {
            if (!topic.getCategory().getOwner().getId().value().equals(authUserId)) {
                throw new AuthException("Authenticated TEACHER is not the owner of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        } else if (AuthenticationUtils.isStudent()) {
            boolean isEnrolled = topic.getStudentList().stream()
                    .anyMatch(student -> student.getId().value().equals(authUserId));
            if (!isEnrolled) {
                throw new AuthException("Authenticated STUDENT is not enrolled in any topic of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        }
    }
}
