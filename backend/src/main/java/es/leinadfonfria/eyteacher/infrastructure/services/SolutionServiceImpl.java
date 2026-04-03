package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponseMapper;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionRequest;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionUseCase;
import es.leinadfonfria.eyteacher.application.services.solution.GetSolutionByIdUseCase;
import es.leinadfonfria.eyteacher.application.services.solution.GetSolutionsByTaskUseCase;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.SolutionRepository;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.infrastructure.events.NotificationPublisher;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewSolutionMessage;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements AddSolutionUseCase, GetSolutionsByTaskUseCase, GetSolutionByIdUseCase {

    private final SolutionRepository<Solution> solutionRepository;
    private final TaskRepository<Task> taskRepository;
    private final UserRepository<User> userRepository;
    private final SolutionResponseMapper solutionResponseMapper;
    private final NotificationPublisher notificationPublisher;

    /**
     * Adds a new solution to a task (Only Student Role).
     * @param request The solution details.
     * @return Result containing the solution ID or an error code.
     */
    @Override
    @Transactional
    public Result<Long, Integer> addSolution(AddSolutionRequest request) {
        try {
            if (!AuthenticationUtils.isStudent()) {
                throw new AuthException("User is not a STUDENT", ErrorCode.USER_NOT_STUDENT);
            }

            UUID studentId = AuthenticationUtils.getUserId();
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new AuthException("Student not found", ErrorCode.USER_NOT_FOUND));

            Task task = taskRepository.findById(request.taskId());
            validateUserHasPermission(task);

            Solution solution = Solution.create(request.description(), student, task);
            Solution saved = solutionRepository.save(solution, request.taskId());
            notificationPublisher.publishNewSolution(new NewSolutionMessage(
                    saved.getId(),
                    task.getId(),
                    task.getDescription(),
                    task.getTopic().getName().value(),
                    student.getFullName(),
                    task.getTopic().getCategory().getOwner().getId().value()
            ));
            return Result.ok(saved.getId());
        } catch (AuthException e) {
            log.error("Authentication error during solution addition", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during solution addition", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during solution addition", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves a page of solutions for the given task.
     * Teachers see all student solutions; students see only their own.
     *
     * @param taskId The ID of the task to retrieve solutions for.
     * @param page   Zero-based page number.
     * @param size   Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of solution responses, or an error code.
     */
    @Override
    public Result<PageResponse<SolutionResponse>, Integer> getSolutionsByTask(Long taskId, int page, int size) {
        try {
            var pageResult = AuthenticationUtils.isTeacher()
                    ? solutionRepository.findByTaskId(taskId, page, size)
                    : solutionRepository.findByTaskIdAndStudentId(taskId, AuthenticationUtils.getUserId(), page, size);
            var content = pageResult.content().stream().map(solutionResponseMapper::toSolutionResponse).toList();
            return Result.ok(new PageResponse<>(content, page, size, pageResult.hasNext()));
        } catch (AuthException e) {
            log.error("Authentication error during solutions retrieval", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during solutions retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during solutions retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves a solution by its ID.
     * @param id The ID of the solution.
     * @return Result containing the SolutionResponse or an error code.
     */
    @Override
    public Result<SolutionResponse, Integer> getSolutionById(Long id) {
        try {
            Solution solution = solutionRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Solution not found", ErrorCode.SOLUTION_NOT_FOUND));
            return Result.ok(solutionResponseMapper.toSolutionResponse(solution));
        } catch (NotFoundException e) {
            log.error("Not found error during solution retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during solution retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    private void validateUserHasPermission(Task task) {
        UUID authUserId = AuthenticationUtils.getUserId();
        if (AuthenticationUtils.isTeacher()) {
            throw new AuthException("Authenticated TEACHER can't save solutions", ErrorCode.AUTHENTICATION_ERROR);
        } else if (AuthenticationUtils.isStudent()) {
            boolean isEnrolled = false;
            for (User student : task.getTopic().getStudentList()) {
                if (student.getId().value().equals(authUserId)) {
                    isEnrolled = true;
                    break;
                }
            }
            if (!isEnrolled) {
                throw new AuthException("Authenticated STUDENT is not enrolled in any topic of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        }
    }
}
