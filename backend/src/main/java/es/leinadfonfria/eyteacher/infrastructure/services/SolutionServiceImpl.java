package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponseMapper;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionRequest;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionUseCase;
import es.leinadfonfria.eyteacher.application.services.solution.GetSolutionsByTaskUseCase;
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
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements AddSolutionUseCase, GetSolutionsByTaskUseCase {

    private final SolutionRepository<Solution> solutionRepository;
    private final TaskRepository<Task> taskRepository;
    private final UserRepository<User> userRepository;
    private final SolutionResponseMapper solutionResponseMapper;

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
     * Retrieves solutions by task ID.
     * Teachers can see all solutions, Students can only see their own.
     * @param taskId The ID of the task to retrieve solutions for.
     * @return Result containing a list of SolutionResponse objects or an error code.
     */
    @Override
    public Result<List<SolutionResponse>, Integer> getSolutionsByTask(Long taskId) {
        try {
            List<Solution> solutions;
            if (AuthenticationUtils.isTeacher()) {
                // Teachers can see all solutions
                solutions = solutionRepository.findByTaskId(taskId);
            } else {
                // Students can only see their own solutions
                solutions = solutionRepository.findByTaskIdAndStudentId(taskId, AuthenticationUtils.getUserId());
            }
            return Result.ok(solutions.stream().map(solutionResponseMapper::toSolutionResponse).toList());
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

    private void validateUserHasPermission(Task task) {
        UUID authUserId = AuthenticationUtils.getUserId();
        if (AuthenticationUtils.isTeacher()) {
            throw new AuthException("Authenticated TEACHER can't save solutions", ErrorCode.AUTHENTICATION_ERROR);
        } else if (AuthenticationUtils.isStudent()) {
            boolean isEnrolled = task.getTopic().getStudentList().stream()
                    .anyMatch(student -> student.getId().value().equals(authUserId));
            if (!isEnrolled) {
                throw new AuthException("Authenticated STUDENT is not enrolled in any topic of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        }
    }
}
