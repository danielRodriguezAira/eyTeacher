package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
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
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.UserRepository;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UserJpaEntity ownerJpaEntity;
    private User ownerDomain;
    private CategoryJpaEntity categoryJpaEntity;
    private final UUID ownerUuid = UUID.randomUUID();
    private final String ownerIdStr = ownerUuid.toString();

    @BeforeEach
    void setUp() {
        ownerJpaEntity = UserJpaEntity.builder()
                .id(ownerUuid)
                .email("owner@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .isAdmin(false)
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
                .id(1L)
                .name("Math")
                .description("Mathematics category")
                .owner(ownerJpaEntity)
                .build();

        TopicJpaEntity topicJpaEntity = TopicJpaEntity.builder()
                .id(1L)
                .name("Algebra")
                .description("Algebra topic")
                .category(categoryJpaEntity)
                .studentList(List.of())
                .build();

        categoryJpaEntity.setTopicList(List.of(topicJpaEntity));
    }

    @Nested
    @DisplayName("Tests para el método addCategory")
    class AddCategoryTests {

        @Test
        @DisplayName("Debe crear la categoría correctamente con datos válidos")
        void addCategory_Success() {
            // Arrange
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);

                when(userRepository.findById(ownerUuid)).thenReturn(Optional.of(ownerJpaEntity));
                when(userMapper.toDomain(ownerJpaEntity)).thenReturn(ownerDomain);
                when(categoryMapper.toEntity(any(Category.class))).thenReturn(categoryJpaEntity);
                when(categoryRepository.save(categoryJpaEntity)).thenReturn(categoryJpaEntity);

                // Act
                Result<Long, Integer> result = categoryService.saveCategory(request);

                // Assert
                assertEquals(categoryJpaEntity.getId(), result.getValue());
                verify(userRepository).findById(ownerUuid);
                verify(userMapper).toDomain(ownerJpaEntity);
                verify(categoryMapper).toEntity(any(Category.class));
                verify(categoryRepository).save(categoryJpaEntity);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el ownerId no es un UUID válido")
        void addCategory_InvalidOwnerIdFormat() {
            // Arrange
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", "not-a-uuid");

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);

                // Act
                Result<Long, Integer> result = categoryService.saveCategory(request);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.INVALID_USER_ID_FORMAT, result.getError());
                verifyNoInteractions(userRepository, categoryRepository, categoryMapper, userMapper);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el owner no existe")
        void addCategory_OwnerNotFound() {
            // Arrange
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(userRepository.findById(ownerUuid)).thenReturn(Optional.empty());

                // Act
                Result<Long, Integer> result = categoryService.saveCategory(request);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_OWNER_NOT_FOUND, result.getError());
                verify(userRepository).findById(ownerUuid);
                verifyNoInteractions(categoryRepository, categoryMapper, userMapper);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si ocurre un error inesperado al guardar")
        void addCategory_UnexpectedError() {
            // Arrange
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);

                when(userRepository.findById(ownerUuid)).thenReturn(Optional.of(ownerJpaEntity));
                when(userMapper.toDomain(ownerJpaEntity)).thenReturn(ownerDomain);
                when(categoryMapper.toEntity(any(Category.class))).thenReturn(categoryJpaEntity);
                when(categoryRepository.save(any())).thenThrow(new RuntimeException("DB error"));

                // Act
                Result<Long, Integer> result = categoryService.saveCategory(request);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.UNKNOWN_ERROR, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getCategoriesByOwner")
    class GetCategoriesByOwnerTests {

        @Test
        @DisplayName("Debe retornar la lista de categorías del owner correctamente")
        void getCategoriesByOwner_Success() {
            // Arrange
            when(userRepository.findById(ownerUuid)).thenReturn(Optional.of(ownerJpaEntity));
            when(categoryRepository.findByOwner(ownerJpaEntity)).thenReturn(List.of(categoryJpaEntity));

            // Act
            Result<List<GetCategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            // Assert
            assertFalse(result.isFailure());
            List<GetCategoryResponse> categories = result.getValue();
            assertEquals(1, categories.size());
            assertEquals(1L, categories.getFirst().id());
            assertEquals("Math", categories.getFirst().name());
            assertEquals("Mathematics category", categories.getFirst().description());
            assertEquals(ownerIdStr, categories.getFirst().ownerId());
            assertTrue(categories.getFirst().topicList().isEmpty());
            verify(userRepository).findById(ownerUuid);
            verify(categoryRepository).findByOwner(ownerJpaEntity);
        }

        @Test
        @DisplayName("Debe retornar lista vacía si el owner no tiene categorías")
        void getCategoriesByOwner_EmptyList() {
            // Arrange
            when(userRepository.findById(ownerUuid)).thenReturn(Optional.of(ownerJpaEntity));
            when(categoryRepository.findByOwner(ownerJpaEntity)).thenReturn(List.of());

            // Act
            Result<List<GetCategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            // Assert
            assertFalse(result.isFailure());
            assertTrue(result.getValue().isEmpty());
        }

        @Test
        @DisplayName("Debe retornar fallo si el ownerId no es un UUID válido")
        void getCategoriesByOwner_InvalidOwnerIdFormat() {
            // Act
            Result<List<GetCategoryResponse>, Integer> result = categoryService.getCategoriesByOwner("not-a-uuid");

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.INVALID_USER_ID_FORMAT, result.getError());
            verifyNoInteractions(userRepository, categoryRepository, categoryMapper);
        }

        @Test
        @DisplayName("Debe retornar fallo si el owner no existe")
        void getCategoriesByOwner_OwnerNotFound() {
            // Arrange
            when(userRepository.findById(ownerUuid)).thenReturn(Optional.empty());

            // Act
            Result<List<GetCategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.CATEGORY_OWNER_NOT_FOUND, result.getError());
            verify(userRepository).findById(ownerUuid);
            verifyNoInteractions(categoryRepository, categoryMapper);
        }

        @Test
        @DisplayName("Debe retornar fallo si ocurre un error inesperado")
        void getCategoriesByOwner_UnexpectedError() {
            // Arrange
            when(userRepository.findById(ownerUuid)).thenReturn(Optional.of(ownerJpaEntity));
            when(categoryRepository.findByOwner(ownerJpaEntity)).thenThrow(new RuntimeException("DB error"));

            // Act
            Result<List<GetCategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(ErrorCode.UNKNOWN_ERROR, result.getError());
        }
    }

    @Nested
    @DisplayName("GetCategory Tests")
    class GetCategoryTests {

        @Test
        @DisplayName("Debe retornar la categoría cuando el profesor es el dueño")
        void getCategory_Teacher_Success() {
            // Arrange
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);

                when(categoryRepository.findById(categoryJpaEntity.getId())).thenReturn(Optional.of(categoryJpaEntity));
                when(userMapper.toDomain(ownerJpaEntity)).thenReturn(ownerDomain);

                Topic topicDomain = Topic.create(new Name("Algebra"), "Algebra topic", null, List.of());
                List<TopicJpaEntity> topicJpaEntityList = categoryJpaEntity.getTopicList();
                when(topicMapper.toDomainList(topicJpaEntityList)).thenReturn(List.of(topicDomain));

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isSuccess());
                assertEquals(categoryJpaEntity.getId(), result.getValue().id());
                assertEquals(ownerIdStr, result.getValue().ownerId());
                verify(categoryRepository).findById(categoryJpaEntity.getId());
            }
        }

        @Test
        @DisplayName("Debe retornar la categoría cuando el estudiante está suscrito")
        void getCategory_Student_Success() {
            // Arrange
            UUID studentUuid = UUID.randomUUID();
            UserJpaEntity studentJpa = UserJpaEntity.builder().id(studentUuid).build();
            categoryJpaEntity.getTopicList().get(0).setStudentList(List.of(studentJpa));

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(true);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(studentUuid);

                when(categoryRepository.findById(categoryJpaEntity.getId())).thenReturn(Optional.of(categoryJpaEntity));
                when(userMapper.toDomain(ownerJpaEntity)).thenReturn(ownerDomain);

                Topic topicDomain = Topic.create(new Name("Algebra"), "Algebra topic", null, List.of());
                when(topicRepository.findTopicByCategoryIdAndStudentId(categoryJpaEntity.getId(), studentUuid))
                        .thenReturn(categoryJpaEntity.getTopicList());
                when(topicMapper.toDomainList(any())).thenReturn(List.of(topicDomain));

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isSuccess());
                assertEquals(categoryJpaEntity.getId(), result.getValue().id());
                verify(categoryRepository).findById(categoryJpaEntity.getId());
                verify(topicRepository).findTopicByCategoryIdAndStudentId(categoryJpaEntity.getId(), studentUuid);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el profesor NO es el dueño")
        void getCategory_Teacher_NotOwner() {
            // Arrange
            UUID otherTeacherUuid = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(otherTeacherUuid);

                when(categoryRepository.findById(categoryJpaEntity.getId())).thenReturn(Optional.of(categoryJpaEntity));

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.AUTHENTICATION_ERROR, result.getError());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el estudiante NO está suscrito")
        void getCategory_Student_NotSubscribed() {
            // Arrange
            UUID otherStudentUuid = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(true);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(otherStudentUuid);

                when(categoryRepository.findById(categoryJpaEntity.getId())).thenReturn(Optional.of(categoryJpaEntity));

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.AUTHENTICATION_ERROR, result.getError());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si la categoría no existe")
        void getCategory_NotFound() {
            // Arrange
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                when(categoryRepository.findById(categoryJpaEntity.getId())).thenReturn(Optional.empty());

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_NOT_FOUND, result.getError());
                verify(categoryRepository).findById(categoryJpaEntity.getId());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si ocurre un error inesperado")
        void getCategory_UnexpectedError() {
            // Arrange
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);
                when(categoryRepository.findById(categoryJpaEntity.getId())).thenThrow(new RuntimeException("DB error"));

                // Act
                Result<GetCategoryResponse, Integer> result = categoryService.getCategory(categoryJpaEntity.getId());

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.UNKNOWN_ERROR, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getCategoriesByStudent")
    class GetCategoriesByStudentTests {

        @Test
        @DisplayName("Debe devolver las categorías de un estudiante correctamente")
        void getCategoriesByStudent_Success() {
            UUID studentId = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
                mockedAuth.when(AuthenticationUtils::isStudent).thenReturn(true);
                when(categoryRepository.findByStudentId(studentId)).thenReturn(List.of(categoryJpaEntity));

                var result = categoryService.getCategoriesByStudent(studentId.toString());

                assertTrue(result.isSuccess());
                assertEquals(1, result.getValue().size());
                assertEquals(categoryJpaEntity.getName(), result.getValue().get(0).name());
                verify(categoryRepository).findByStudentId(studentId);
            }
        }

        @Test
        @DisplayName("Debe devolver una lista vacía si el estudiante no tiene categorías")
        void getCategoriesByStudent_EmptyList() {
            UUID studentId = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
                mockedAuth.when(AuthenticationUtils::isStudent).thenReturn(true);
                when(categoryRepository.findByStudentId(studentId)).thenReturn(List.of());

                var result = categoryService.getCategoriesByStudent(studentId.toString());

                assertTrue(result.isSuccess());
                assertTrue(result.getValue().isEmpty());
                verify(categoryRepository).findByStudentId(studentId);
            }
        }

        @Test
        @DisplayName("Debe devolver fallo si el usuario no es estudiante")
        void getCategoriesByStudent_NotAStudent() {
            UUID studentId = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
                mockedAuth.when(AuthenticationUtils::isStudent).thenReturn(false);

                var result = categoryService.getCategoriesByStudent(studentId.toString());

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_STUDENT, result.getError());
                verifyNoInteractions(categoryRepository);
            }
        }

        @Test
        @DisplayName("Debe fallar si ocurre un error inesperado al obtener categorías por estudiante")
        void getCategoriesByStudent_UnexpectedError() {
            UUID studentId = UUID.randomUUID();
            try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
                mockedAuth.when(AuthenticationUtils::isStudent).thenReturn(true);
                when(categoryRepository.findByStudentId(studentId)).thenThrow(new RuntimeException("DB error"));

                var result = categoryService.getCategoriesByStudent(studentId.toString());

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.UNKNOWN_ERROR, result.getError());
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método deleteCategory")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Debe eliminar la categoría correctamente si el usuario es TEACHER")
        void deleteCategory_Success() {
            // Arrange
            Long categoryId = 1L;
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepository.existsById(categoryId)).thenReturn(true);

                // Act
                Result<Void, Integer> result = categoryService.deleteCategory(categoryId);

                // Assert
                assertTrue(result.isSuccess());
                verify(categoryRepository).existsById(categoryId);
                verify(categoryRepository).deleteById(categoryId);
            }
        }

        @Test
        @DisplayName("Debe fallar si el usuario no es TEACHER")
        void deleteCategory_NotTeacher() {
            // Arrange
            Long categoryId = 1L;
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                // Act
                Result<Void, Integer> result = categoryService.deleteCategory(categoryId);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_TEACHER, result.getError());
                verify(categoryRepository, never()).deleteById(any());
            }
        }

        @Test
        @DisplayName("Debe fallar si la categoría no existe")
        void deleteCategory_NotFound() {
            // Arrange
            Long categoryId = 1L;
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepository.existsById(categoryId)).thenReturn(false);

                // Act
                Result<Void, Integer> result = categoryService.deleteCategory(categoryId);

                // Assert
                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_NOT_FOUND, result.getError());
                verify(categoryRepository, never()).deleteById(any());
            }
        }
    }
}
