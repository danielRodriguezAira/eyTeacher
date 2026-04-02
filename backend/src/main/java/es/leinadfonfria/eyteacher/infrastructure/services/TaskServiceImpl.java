package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.task.*;
import es.leinadfonfria.eyteacher.application.services.task.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.ports.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.events.NotificationPublisher;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewTaskMessage;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements SaveTaskUseCase, GetTaskUseCase, GetTasksByTopicUseCase, DeleteTaskUseCase, GetTasksByStudentIdUseCase {

    private static final String STATUS_WITHOUT_SOLUTION = "WITHOUT_SOLUTION";
    private static final String STATUS_WITHOUT_CORRECTION = "WITHOUT_CORRECTION";
    private static final String STATUS_CORRECTED = "CORRECTED";

    private final TaskRepository<Task> taskRepository;
    private final TopicRepository<Topic> topicRepository;
    private final TaskResponseMapper taskResponseMapper;
    private final SolutionResponseMapper solutionResponseMapper;
    private final NotificationPublisher notificationPublisher;

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
            List<UUID> studentIds = topic.getStudentList().stream()
                    .map(s -> s.getId().value())
                    .toList();
            notificationPublisher.publishNewTask(new NewTaskMessage(
                    saved.getId(),
                    saved.getDescription(),
                    topic.getName().value(),
                    topic.getCategory().getOwner().getFullName(),
                    studentIds
            ));
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
     * Retrieves all tasks accessible to a student, grouped by status, then by category, then by topic.
     * Only the authenticated student may query their own tasks; teachers may query any student.
     *
     * @param studentId The string representation of the student's UUID.
     * @return Result containing the grouped {@link StudentTasksResponse} list, or an error code on failure.
     */
    @Override
    public Result<List<StudentTasksResponse>, Integer> getTasksByStudentId(String studentId) {
        try {
            UUID studentUuid = UUID.fromString(studentId);

            if (AuthenticationUtils.isStudent() && !AuthenticationUtils.getUserId().equals(studentUuid)) {
                throw new AuthException("Student can only view their own tasks", ErrorCode.AUTHENTICATION_ERROR);
            }

            List<Task> tasks;
            if (AuthenticationUtils.isTeacher()) {
                tasks = taskRepository.findByStudentIdAndTeacherId(studentUuid, AuthenticationUtils.getUserId());
            } else {
                tasks = taskRepository.findByStudentId(studentUuid);
            }

            Map<String, List<Task>> tasksByStatus = new LinkedHashMap<>();
            tasksByStatus.put(STATUS_WITHOUT_SOLUTION, new ArrayList<>());
            tasksByStatus.put(STATUS_WITHOUT_CORRECTION, new ArrayList<>());
            tasksByStatus.put(STATUS_CORRECTED, new ArrayList<>());

            for (Task task : tasks) {
                if (task.getSolutionList().isEmpty()) {
                    tasksByStatus.get(STATUS_WITHOUT_SOLUTION).add(task);
                } else if (task.getSolutionList().get(0).getCorrection() == null) {
                    tasksByStatus.get(STATUS_WITHOUT_CORRECTION).add(task);
                } else {
                    tasksByStatus.get(STATUS_CORRECTED).add(task);
                }
            }

            List<StudentTasksResponse> response = tasksByStatus.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .map(entry -> new StudentTasksResponse(entry.getKey(), buildCategoryGroups(entry.getValue())))
                    .toList();

            return Result.ok(response);
        } catch (AuthException e) {
            log.error("Authentication error during student tasks retrieval", e);
            return Result.fail(e.getCode());
        } catch (IllegalArgumentException e) {
            log.error("Invalid student ID format: {}", studentId, e);
            return Result.fail(ErrorCode.INVALID_USER_ID_FORMAT);
        } catch (Exception e) {
            log.error("Unexpected error during student tasks retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Builds the list of category groups from a flat list of tasks, sub-grouping by topic within each category.
     *
     * @param tasks The list of tasks belonging to the same status group.
     * @return List of {@link CategoryTaskGroup} objects.
     */
    private List<CategoryTaskGroup> buildCategoryGroups(List<Task> tasks) {
        Map<Long, List<Task>> tasksByCategory = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getTopic().getCategory().getId(),
                        LinkedHashMap::new, Collectors.toList()));

        return tasksByCategory.entrySet().stream()
                .map(catEntry -> {
                    List<Task> catTasks = catEntry.getValue();
                    String categoryName = catTasks.getFirst().getTopic().getCategory().getName().value();

                    Map<Long, List<Task>> tasksByTopic = catTasks.stream()
                            .collect(Collectors.groupingBy(t -> t.getTopic().getId(),
                                    LinkedHashMap::new, Collectors.toList()));

                    List<TopicTaskGroup> topicGroups = tasksByTopic.entrySet().stream()
                            .map(topicEntry -> {
                                List<Task> topicTasks = topicEntry.getValue();
                                String topicName = topicTasks.getFirst().getTopic().getName().value();

                                List<StudentTaskItem> taskItems = topicTasks.stream()
                                        .map(t -> {
                                            SolutionResponse solution = t.getSolutionList().isEmpty() ? null
                                                    : solutionResponseMapper.toSolutionResponse(t.getSolutionList().get(0));
                                            return new StudentTaskItem(t.getId(), t.getDescription(), solution);
                                        })
                                        .toList();

                                return new TopicTaskGroup(topicEntry.getKey(), topicName, taskItems);
                            })
                            .toList();

                    return new CategoryTaskGroup(catEntry.getKey(), categoryName, topicGroups);
                })
                .toList();
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
