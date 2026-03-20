package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponse;
import es.leinadfonfria.eyteacher.application.dtos.solution.SolutionResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.*;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.SolutionRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.TaskRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.UserRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceImplTest {

    @Mock
    private SolutionRepositoryAdapter solutionRepositoryAdapter;

    @Mock
    private TaskRepositoryAdapter taskRepositoryAdapter;

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private SolutionResponseMapper solutionResponseMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SolutionServiceImpl solutionService;

    private User studentDomain;
    private Task taskDomain;
    private Topic topicDomain;
    private Solution solutionDomain;

    private final UUID studentUuid = UUID.randomUUID();
    private final Long taskId = 1L;
    private final Long solutionId = 1L;

    @BeforeEach
    void setUp() {
        User ownerDomain = User.create(
                new UserId(UUID.randomUUID()),
                new Email("teacher@example.com"),
                Password.hashed("password"),
                new Name("John"),
                new Name("Doe"),
                false
        );

        studentDomain = User.create(
                new UserId(studentUuid),
                new Email("student@example.com"),
                Password.hashed("password"),
                new Name("Jane"),
                new Name("Smith"),
                false
        );

        Category categoryDomain = Category.edit(1L, new Name("Math"), "Math category", ownerDomain);

        topicDomain = Topic.edit(1L, new Name("Algebra"), "Basic algebra", categoryDomain, List.of(studentDomain), List.of());

        taskDomain = Task.create("Solve equations", topicDomain);

        solutionDomain = Solution.builder()
                .id(solutionId)
                .description("My solution")
                .student(studentDomain)
                .task(taskDomain)
                .build();
    }

    @Nested
    @DisplayName("Tests para el método addSolution")
    class AddSolutionTests {

        @Test
        @DisplayName("Debe añadir una solución correctamente")
        void addSolution_Success() {
            AddSolutionRequest request = new AddSolutionRequest("My solution", taskId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(true);
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(studentUuid);

                when(userRepositoryAdapter.findById(studentUuid)).thenReturn(Optional.of(studentDomain));
                
                List<User> students = List.of(studentDomain);
                Topic topicWithStudent = Topic.edit(
                        topicDomain.getId(),
                        topicDomain.getName(),
                        topicDomain.getDescription(),
                        topicDomain.getCategory(),
                        students,
                        List.of()
                );
                Task taskWithTopicWithStudent = Task.create(taskId, "Task Description", topicWithStudent, List.of());
                when(taskRepositoryAdapter.findById(taskId)).thenReturn(taskWithTopicWithStudent);
                
                when(solutionRepositoryAdapter.save(any(Solution.class), eq(taskId))).thenAnswer(invocation -> {
                    Solution solutionArg = invocation.getArgument(0);
                    return Solution.builder()
                            .id(solutionId)
                            .description(solutionArg.getDescription())
                            .student(solutionArg.getStudent())
                            .task(solutionArg.getTask())
                            .build();
                });

                Result<Long, Integer> result = solutionService.addSolution(request);

                assertTrue(result.isSuccess());
                assertEquals(solutionId, result.getValue());
                verify(solutionRepositoryAdapter).save(any(Solution.class), eq(taskId));
            }
        }

        @Test
        @DisplayName("Debe fallar si el usuario no es STUDENT")
        void addSolution_NotStudent() {
            AddSolutionRequest request = new AddSolutionRequest("My solution", taskId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);

                Result<Long, Integer> result = solutionService.addSolution(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_STUDENT, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getSolutionsByTask")
    class GetSolutionsByTaskTests {

        @Test
        @DisplayName("Debe añadir una solución correctamente")
        void getSolutionsByTask_Success() {
            UserResponse student = new UserResponse(studentDomain.getId().value().toString(), "Jane", "Smith", "jane.smith@example.com");
            TaskResponse task = new TaskResponse(taskId, "Task description", 1L, Collections.emptyList());
            SolutionResponse solutionResponse = new SolutionResponse(solutionId, "My solution", student, task, null);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(solutionRepositoryAdapter.findByTaskId(taskId)).thenReturn(List.of(solutionDomain));
                when(solutionResponseMapper.toSolutionResponse(solutionDomain)).thenReturn(solutionResponse);

                Result<List<SolutionResponse>, Integer> result = solutionService.getSolutionsByTask(taskId);

                assertTrue(result.isSuccess());
                assertEquals(1, result.getValue().size());
                assertEquals(solutionId, result.getValue().getFirst().id());
            }
        }
    }
}
