package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponse;
import es.leinadfonfria.eyteacher.application.dtos.task.TaskResponseMapper;
import es.leinadfonfria.eyteacher.application.services.task.SaveTaskRequest;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.shared.PageResult;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.TaskRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.TopicRepositoryAdapter;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepositoryAdapter taskRepositoryAdapter;

    @Mock
    private TopicRepositoryAdapter topicRepositoryAdapter;

    @Mock
    private TaskResponseMapper taskResponseMapper;


    @InjectMocks
    private TaskServiceImpl taskService;

    private Topic topicDomain;
    private Task taskDomain;

    private final UUID ownerUuid = UUID.randomUUID();
    private final Long topicId = 1L;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        User ownerDomain = User.create(
                new UserId(ownerUuid),
                new Email("teacher@example.com"),
                Password.hashed("password"),
                new Name("John"),
                new Name("Doe"),
                false
        );

        Category categoryDomain = Category.edit(1L, new Name("Math"), "Math category", ownerDomain);

        topicDomain = Topic.edit(topicId, new Name("Algebra"), "Basic algebra", categoryDomain, List.of(), List.of());

        taskDomain = Task.edit(taskId, "Solve equations");
    }

    @Nested
    @DisplayName("Tests para el método saveTask")
    class SaveTaskTests {

        @Test
        @DisplayName("Debe crear una task correctamente")
        void saveTask_CreateSuccess() {
            SaveTaskRequest request = new SaveTaskRequest(null, "Solve equations", topicId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
                when(taskRepositoryAdapter.save(any(Task.class), eq(topicId))).thenReturn(taskDomain);

                Result<Long, Integer> result = taskService.saveTask(request);

                assertTrue(result.isSuccess());
                assertEquals(taskId, result.getValue());
                verify(taskRepositoryAdapter).save(any(Task.class), eq(topicId));
            }
        }

        @Test
        @DisplayName("Debe actualizar una task correctamente")
        void saveTask_UpdateSuccess() {
            SaveTaskRequest request = new SaveTaskRequest(taskId, "Updated description", topicId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
                when(taskRepositoryAdapter.existsById(taskId)).thenReturn(true);
                when(taskRepositoryAdapter.save(any(Task.class), eq(topicId))).thenReturn(taskDomain);

                Result<Long, Integer> result = taskService.saveTask(request);

                assertTrue(result.isSuccess());
                assertEquals(taskId, result.getValue());
                verify(taskRepositoryAdapter).save(any(Task.class), eq(topicId));
            }
        }

        @Test
        @DisplayName("Debe fallar si el usuario no es TEACHER")
        void saveTask_NotTeacher() {
            SaveTaskRequest request = new SaveTaskRequest(null, "Solve equations", topicId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                Result<Long, Integer> result = taskService.saveTask(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_TEACHER, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getTasksByTopic")
    class GetTasksByTopicTests {

        @Test
        @DisplayName("Debe retornar la lista de tasks para un topic")
        void getTasksByTopic_Success() {
            TaskResponse taskResponse = new TaskResponse(taskId, "Solve equations", topicId, Collections.emptyList(), LocalDateTime.now());

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
                when(taskRepositoryAdapter.findByTopicId(topicId, 0, 10)).thenReturn(new PageResult<>(List.of(taskDomain), false));
                when(taskResponseMapper.toTaskResponse(taskDomain)).thenReturn(taskResponse);

                Result<PageResponse<TaskResponse>, Integer> result = taskService.getTasksByTopic(topicId, 0, 10);

                assertTrue(result.isSuccess());
                assertEquals(1, result.getValue().content().size());
                assertEquals(taskId, result.getValue().content().getFirst().id());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método deleteTask")
    class DeleteTaskTests {

        @Test
        @DisplayName("Debe eliminar una task correctamente")
        void deleteTask_Success() {
            Task taskWithTopic = Task.create("Solve equations", topicDomain);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(taskRepositoryAdapter.findById(taskId)).thenReturn(taskWithTopic);

                Result<Void, Integer> result = taskService.deleteTask(taskId);

                assertTrue(result.isSuccess());
                verify(taskRepositoryAdapter).delete(taskId);
            }
        }
    }
}
