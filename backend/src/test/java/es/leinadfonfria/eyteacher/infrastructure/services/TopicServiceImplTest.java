package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.topic.AddTopicSubscriptionToStudentsRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponseMapper;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.events.NotificationPublisher;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.CategoryRepositoryAdapter;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepositoryAdapter topicRepositoryAdapter;
    @Mock
    private CategoryRepositoryAdapter categoryRepositoryAdapter;
    @Mock
    private TopicResponseMapper topicResponseMapper;
    @Mock
    private TaskRepository<Task> taskRepository;
    @Mock
    private UserRepository<User> userRepository;
    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private TopicServiceImpl topicService;

    private Category categoryDomain;
    private Topic topicDomain;
    private final UUID ownerUuid = UUID.randomUUID();
    private final Long categoryId = 1L;
    private final Long topicId = 1L;

    @BeforeEach
    void setUp() {
        User ownerDomain = User.create(
                new UserId(ownerUuid),
                new Email("owner@example.com"),
                Password.hashed("encodedPassword"),
                new Name("John"),
                new Name("Doe"),
                false
        );
        categoryDomain = Category.edit(categoryId, new Name("Math"), "Mathematics category", ownerDomain);
        topicDomain = Topic.edit(topicId, new Name("Algebra"), "Algebra topic", categoryDomain, List.of(), List.of());
    }

    @Nested
    @DisplayName("Tests para el método saveTopic")
    class SaveTopicTests {

        @Test
        @DisplayName("Debe crear el tópico correctamente")
        void saveTopic_CreateSuccess() {
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Algebra topic", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.findById(categoryId)).thenReturn(categoryDomain);
                when(topicRepositoryAdapter.save(any(Topic.class))).thenReturn(topicDomain);

                Result<Long, Integer> result = topicService.saveTopic(request);

                assertFalse(result.isFailure());
                assertEquals(topicId, result.getValue());
                verify(categoryRepositoryAdapter).findById(categoryId);
                verify(topicRepositoryAdapter).save(any(Topic.class));
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es TEACHER")
        void saveTopic_NotTeacher() {
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Algebra topic", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                Result<Long, Integer> result = topicService.saveTopic(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_OWNER_NOT_TEACHER, result.getError());
                verifyNoInteractions(categoryRepositoryAdapter, topicRepositoryAdapter);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si la categoría no existe")
        void saveTopic_CategoryNotFound() {
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Algebra topic", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.findById(categoryId))
                        .thenThrow(new NotFoundException("Category not found", ErrorCode.TOPIC_CATEGORY_NOT_FOUND));

                Result<Long, Integer> result = topicService.saveTopic(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_CATEGORY_NOT_FOUND, result.getError());
                verifyNoInteractions(topicRepositoryAdapter);
            }
        }

        @Test
        @DisplayName("Debe actualizar el tópico correctamente")
        void saveTopic_UpdateSuccess() {
            SaveTopicRequest request = new SaveTopicRequest(topicId, "Algebra Updated", "Updated description", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.findById(categoryId)).thenReturn(categoryDomain);
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
                when(topicRepositoryAdapter.save(any(Topic.class))).thenReturn(topicDomain);

                Result<Long, Integer> result = topicService.saveTopic(request);

                assertFalse(result.isFailure());
                verify(topicRepositoryAdapter).findById(topicId);
                verify(topicRepositoryAdapter).save(any(Topic.class));
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getTopic")
    class GetTopicTests {

        @Test
        @DisplayName("Debe retornar el tópico correctamente")
        void getTopic_Success() {
            TopicResponse topicResponse = new TopicResponse(topicId, "Algebra", "Algebra topic", categoryId, List.of(), List.of());

            when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
            when(taskRepository.findByTopicId(topicId)).thenReturn(List.of());
            when(topicResponseMapper.toTopicResponse(eq(topicDomain), any())).thenReturn(topicResponse);

            Result<TopicResponse, Integer> result = topicService.getTopic(topicId);

            assertFalse(result.isFailure());
            assertEquals(topicId, result.getValue().id());
            verify(topicRepositoryAdapter).findById(topicId);
            verify(topicResponseMapper).toTopicResponse(eq(topicDomain), any());
        }

        @Test
        @DisplayName("Debe retornar fallo si el tópico no existe")
        void getTopic_NotFound() {
            when(topicRepositoryAdapter.findById(99L))
                    .thenThrow(new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));

            Result<TopicResponse, Integer> result = topicService.getTopic(99L);

            assertTrue(result.isFailure());
            assertEquals(ErrorCode.TOPIC_NOT_FOUND, result.getError());
        }
    }

    @Nested
    @DisplayName("Tests para el método getTopicsByCategory")
    class GetTopicsByCategoryTests {

        @Test
        @DisplayName("Debe retornar la lista de tópicos correctamente")
        void getTopics_Success() {
            TopicResponse topicResponse = new TopicResponse(topicId, "Algebra", "Algebra topic", categoryId, List.of(), List.of());

            when(categoryRepositoryAdapter.findById(categoryId)).thenReturn(categoryDomain);
            when(topicRepositoryAdapter.findByCategory(categoryId)).thenReturn(List.of(topicDomain));
            when(topicResponseMapper.toTopicResponseList(List.of(topicDomain))).thenReturn(List.of(topicResponse));

            Result<List<TopicResponse>, Integer> result = topicService.getTopicsByCategory(ownerUuid.toString(), categoryId);

            assertFalse(result.isFailure());
            assertEquals(1, result.getValue().size());
            verify(topicRepositoryAdapter).findByCategory(categoryId);
        }

        @Test
        @DisplayName("Debe retornar fallo si el owner no coincide")
        void getTopics_OwnerMismatch() {
            when(categoryRepositoryAdapter.findById(categoryId)).thenReturn(categoryDomain);

            Result<List<TopicResponse>, Integer> result = topicService.getTopicsByCategory(UUID.randomUUID().toString(), categoryId);

            assertTrue(result.isFailure());
            assertEquals(ErrorCode.CATEGORY_USER_NOT_FOUND, result.getError());
        }
    }

    @Nested
    @DisplayName("Tests para el método deleteTopic")
    class DeleteTopicTests {

        @Test
        @DisplayName("Debe eliminar el tópico correctamente")
        void deleteTopic_Success() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(topicRepositoryAdapter.existsById(topicId)).thenReturn(true);

                Result<Void, Integer> result = topicService.deleteTopic(topicId);

                assertFalse(result.isFailure());
                verify(topicRepositoryAdapter).delete(topicId);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el tópico no existe")
        void deleteTopic_NotFound() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(topicRepositoryAdapter.existsById(99L)).thenReturn(false);

                Result<Void, Integer> result = topicService.deleteTopic(99L);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_NOT_FOUND, result.getError());
                verify(topicRepositoryAdapter, never()).delete(any());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es TEACHER")
        void deleteTopic_NotTeacher() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                Result<Void, Integer> result = topicService.deleteTopic(topicId);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_OWNER_NOT_TEACHER, result.getError());
                verify(topicRepositoryAdapter, never()).delete(any());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método addTopicSubscriptionToStudents")
    class AddTopicSubscriptionToStudentsTests {

        @Test
        @DisplayName("Debe añadir suscripciones correctamente")
        void addTopicSubscriptionToStudents_Success() {
            String studentEmail = "student@example.com";
            AddTopicSubscriptionToStudentsRequest request = new AddTopicSubscriptionToStudentsRequest(topicId, ownerUuid, List.of(studentEmail));
            User studentDomain = User.create(new UserId(UUID.randomUUID()), new Email(studentEmail), Password.hashed("pass"), new Name("Student"), new Name("One"), true);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);
                when(userRepository.findByEmail(studentEmail)).thenReturn(java.util.Optional.of(studentDomain));

                Result<Void, Integer> result = topicService.addTopicSubscriptionToStudents(request);

                assertFalse(result.isFailure());
                verify(topicRepositoryAdapter).save(any(Topic.class));
                verify(notificationPublisher, times(1)).publishNewSubscription(any());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es el dueño")
        void addTopicSubscriptionToStudents_NotOwner() {
            String studentEmail = "student@example.com";
            AddTopicSubscriptionToStudentsRequest request = new AddTopicSubscriptionToStudentsRequest(topicId, ownerUuid, List.of(studentEmail));

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(UUID.randomUUID());
                when(topicRepositoryAdapter.findById(topicId)).thenReturn(topicDomain);

                Result<Void, Integer> result = topicService.addTopicSubscriptionToStudents(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.AUTHENTICATION_ERROR, result.getError());
                verify(topicRepositoryAdapter, never()).save(any());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el tópico no existe")
        void addTopicSubscriptionToStudents_TopicNotFound() {
            String studentEmail = "student@example.com";
            AddTopicSubscriptionToStudentsRequest request = new AddTopicSubscriptionToStudentsRequest(topicId, ownerUuid, List.of(studentEmail));

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(topicRepositoryAdapter.findById(topicId)).thenThrow(new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));

                Result<Void, Integer> result = topicService.addTopicSubscriptionToStudents(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_NOT_FOUND, result.getError());
            }
        }
    }
}
