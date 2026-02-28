package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.CategoryMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TopicMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private TopicServiceImpl topicService;

    private UserJpaEntity ownerJpaEntity;
    private User ownerDomain;
    private CategoryJpaEntity categoryJpaEntity;
    private Category categoryDomain;
    private TopicJpaEntity topicJpaEntity;
    private Topic topicDomain;
    private final UUID ownerUuid = UUID.randomUUID();
    private final String ownerIdStr = ownerUuid.toString();
    private final Long categoryId = 1L;
    private final Long topicId = 1L;

    @BeforeEach
    void setUp() {
        ownerJpaEntity = UserJpaEntity.builder()
                .id(ownerUuid)
                .email("owner@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        ownerDomain = User.create(
                new UserId(ownerUuid),
                new Email("owner@example.com"),
                Password.hashed("encodedPassword"),
                new Name("John"),
                new Name("Doe"),
                false
        );

        categoryJpaEntity = CategoryJpaEntity.builder()
                .id(categoryId)
                .name("Math")
                .owner(ownerJpaEntity)
                .build();

        categoryDomain = Category.edit(categoryId, "Math", "Math category", ownerDomain);

        topicJpaEntity = TopicJpaEntity.builder()
                .id(topicId)
                .name("Algebra")
                .description("Basic algebra")
                .category(categoryJpaEntity)
                .build();

        topicDomain = Topic.edit(topicId, "Algebra", "Basic algebra", categoryDomain);
    }

    @Nested
    @DisplayName("Tests para el método saveTopic")
    class SaveTopicTests {

        @Test
        @DisplayName("Debe crear un topic correctamente")
        void saveTopic_CreateSuccess() {
            // Arrange
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Basic algebra", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryJpaEntity));
                when(categoryMapper.toDomain(categoryJpaEntity)).thenReturn(categoryDomain);
                when(topicMapper.toEntity(any(Topic.class))).thenReturn(topicJpaEntity);
                when(topicRepository.save(any(TopicJpaEntity.class))).thenReturn(topicJpaEntity);

                // Act
                Result<Long, Integer> result = topicService.saveTopic(request);

                // Assert
                assertTrue(result.isSuccess());
                assertEquals(topicId, result.getValue());
                verify(topicRepository).save(any(TopicJpaEntity.class));
            }
        }

        @Test
        @DisplayName("Debe fallar si el usuario no es TEACHER")
        void saveTopic_NotTeacher() {
            // Arrange
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Basic algebra", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                // Act
                Result<Long, Integer> result = topicService.saveTopic(request);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_OWNER_NOT_TEACHER, result.getError());
            }
        }

        @Test
        @DisplayName("Debe fallar si la categoría no existe")
        void saveTopic_CategoryNotFound() {
            // Arrange
            SaveTopicRequest request = new SaveTopicRequest(null, "Algebra", "Basic algebra", categoryId);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

                // Act
                Result<Long, Integer> result = topicService.saveTopic(request);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_CATEGORY_NOT_FOUND, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getTopic")
    class GetTopicTests {

        @Test
        @DisplayName("Debe retornar el topic si existe")
        void getTopic_Success() {
            // Arrange
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(topicJpaEntity));
            when(topicMapper.toDomain(topicJpaEntity)).thenReturn(topicDomain);

            // Act
            Result<GetTopicResponse, Integer> result = topicService.getTopic(topicId);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals(topicId, result.getValue().id());
            assertEquals("Algebra", result.getValue().name());
        }

        @Test
        @DisplayName("Debe fallar si el topic no existe")
        void getTopic_NotFound() {
            // Arrange
            when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

            // Act
            Result<GetTopicResponse, Integer> result = topicService.getTopic(topicId);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.TOPIC_NOT_FOUND, result.getError());
        }
    }

    @Nested
    @DisplayName("Tests para el método getTopicsByOwnerAndCategory")
    class GetTopicsByOwnerAndCategoryTests {

        @Test
        @DisplayName("Debe retornar la lista de topics")
        void getTopics_Success() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryJpaEntity));
            when(topicRepository.findByCategory(categoryJpaEntity)).thenReturn(List.of(topicJpaEntity));
            when(topicMapper.toDomain(topicJpaEntity)).thenReturn(topicDomain);

            // Act
            Result<List<GetTopicResponse>, Integer> result = topicService.getTopicsByCategory(ownerIdStr, categoryId);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals(1, result.getValue().size());
            assertEquals(topicId, result.getValue().get(0).id());
        }
    }

    @Nested
    @DisplayName("Tests para el método deleteTopic")
    class DeleteTopicTests {

        @Test
        @DisplayName("Debe eliminar el topic correctamente")
        void deleteTopic_Success() {
            // Arrange
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(topicRepository.existsById(topicId)).thenReturn(true);

                // Act
                Result<Void, Integer> result = topicService.deleteTopic(topicId);

                // Assert
                assertTrue(result.isSuccess());
                verify(topicRepository).deleteById(topicId);
            }
        }

        @Test
        @DisplayName("Debe fallar si el topic no existe")
        void deleteTopic_NotFound() {
            // Arrange
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(topicRepository.existsById(topicId)).thenReturn(false);

                // Act
                Result<Void, Integer> result = topicService.deleteTopic(topicId);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.TOPIC_NOT_FOUND, result.getError());
            }
        }
    }
}
