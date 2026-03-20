package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponse;
import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponseMapper;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.CategoryRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.TopicRepositoryAdapter;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepositoryAdapter categoryRepositoryAdapter;
    @Mock
    private TopicRepositoryAdapter topicRepositoryAdapter;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private CategoryResponseMapper categoryResponseMapper;
    @Mock
    private UserResponseMapper userResponseMapper;
    @Mock
    private TopicResponseMapper topicResponseMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User ownerDomain;
    private final UUID ownerUuid = UUID.randomUUID();
    private final String ownerIdStr = ownerUuid.toString();

    @BeforeEach
    void setUp() {
        ownerDomain = User.create(
                new UserId(ownerUuid),
                new Email("owner@example.com"),
                Password.hashed("encodedPassword"),
                new Name("John"),
                new Name("Doe"),
                false
        );
    }

    @Nested
    @DisplayName("Tests para el método saveCategory")
    class SaveCategoryTests {

        @Test
        @DisplayName("Debe crear la categoría correctamente con datos válidos")
        void saveCategory_Success() {
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);
            Category savedCategory = Category.edit(1L, new Name("Math"), "Mathematics category", ownerDomain);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(userRepositoryAdapter.findById(ownerUuid)).thenReturn(Optional.of(ownerDomain));
                when(categoryRepositoryAdapter.save(any(Category.class))).thenReturn(savedCategory);

                Result<Long, Integer> result = categoryService.saveCategory(request);

                assertFalse(result.isFailure());
                assertEquals(1L, result.getValue());
                verify(userRepositoryAdapter).findById(ownerUuid);
                verify(categoryRepositoryAdapter).save(any(Category.class));
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es TEACHER")
        void saveCategory_NotTeacher() {
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                Result<Long, Integer> result = categoryService.saveCategory(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_TEACHER, result.getError());
                verifyNoInteractions(userRepositoryAdapter, categoryRepositoryAdapter);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el ownerId no es un UUID válido")
        void saveCategory_InvalidOwnerIdFormat() {
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", "not-a-uuid");

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);

                Result<Long, Integer> result = categoryService.saveCategory(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.INVALID_USER_ID_FORMAT, result.getError());
                verifyNoInteractions(userRepositoryAdapter, categoryRepositoryAdapter);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el owner no existe")
        void saveCategory_OwnerNotFound() {
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(userRepositoryAdapter.findById(ownerUuid)).thenReturn(Optional.empty());

                Result<Long, Integer> result = categoryService.saveCategory(request);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_USER_NOT_FOUND, result.getError());
                verify(userRepositoryAdapter).findById(ownerUuid);
                verifyNoInteractions(categoryRepositoryAdapter);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si ocurre un error inesperado al guardar")
        void saveCategory_UnexpectedError() {
            SaveCategoryRequest request = new SaveCategoryRequest("Math", "Mathematics category", ownerIdStr);

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(userRepositoryAdapter.findById(ownerUuid)).thenReturn(Optional.of(ownerDomain));
                when(categoryRepositoryAdapter.save(any(Category.class))).thenThrow(new RuntimeException("DB error"));

                Result<Long, Integer> result = categoryService.saveCategory(request);

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
            Category savedCategory = Category.edit(1L, new Name("Math"), "Mathematics category", ownerDomain);
            CategoryResponse categoryResponse = new CategoryResponse(1L, "Math", "Mathematics category", ownerIdStr, List.of(), List.of());

            when(userRepositoryAdapter.findById(ownerUuid)).thenReturn(Optional.of(ownerDomain));
            when(categoryRepositoryAdapter.findByOwnerId(any(UserId.class))).thenReturn(List.of(savedCategory));
            when(categoryResponseMapper.toCategoryResponseList(List.of(savedCategory))).thenReturn(List.of(categoryResponse));

            Result<List<CategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            assertFalse(result.isFailure());
            assertEquals(1, result.getValue().size());
            assertEquals(1L, result.getValue().getFirst().id());
            verify(userRepositoryAdapter).findById(ownerUuid);
            verify(categoryRepositoryAdapter).findByOwnerId(any(UserId.class));
        }

        @Test
        @DisplayName("Debe retornar fallo si el owner no existe")
        void getCategoriesByOwner_OwnerNotFound() {
            when(userRepositoryAdapter.findById(ownerUuid)).thenReturn(Optional.empty());

            Result<List<CategoryResponse>, Integer> result = categoryService.getCategoriesByOwner(ownerIdStr);

            assertTrue(result.isFailure());
            assertEquals(ErrorCode.CATEGORY_USER_NOT_FOUND, result.getError());
            verifyNoInteractions(categoryRepositoryAdapter);
        }

        @Test
        @DisplayName("Debe retornar fallo si el ownerId no es un UUID válido")
        void getCategoriesByOwner_InvalidOwnerIdFormat() {
            Result<List<CategoryResponse>, Integer> result = categoryService.getCategoriesByOwner("not-a-uuid");

            assertTrue(result.isFailure());
            assertEquals(ErrorCode.INVALID_USER_ID_FORMAT, result.getError());
        }
    }

    @Nested
    @DisplayName("Tests para el método getCategoriesByStudent")
    class GetCategoriesByStudentTests {

        @Test
        @DisplayName("Debe retornar la lista de categorías del estudiante correctamente")
        void getCategoriesByStudent_Success() {
            Category savedCategory = Category.edit(1L, new Name("Math"), "Mathematics category", ownerDomain);
            CategoryResponse categoryResponse = new CategoryResponse(1L, "Math", "Mathematics category", ownerIdStr, List.of(), List.of());

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(true);
                when(categoryRepositoryAdapter.findByStudentId(any(UserId.class))).thenReturn(List.of(savedCategory));
                when(categoryResponseMapper.toCategoryResponseList(List.of(savedCategory))).thenReturn(List.of(categoryResponse));

                Result<List<CategoryResponse>, Integer> result = categoryService.getCategoriesByStudent(ownerIdStr);

                assertFalse(result.isFailure());
                assertEquals(1, result.getValue().size());
                verify(categoryRepositoryAdapter).findByStudentId(any(UserId.class));
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es STUDENT")
        void getCategoriesByStudent_NotStudent() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isStudent).thenReturn(false);

                Result<List<CategoryResponse>, Integer> result = categoryService.getCategoriesByStudent(ownerIdStr);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_STUDENT, result.getError());
                verifyNoInteractions(categoryRepositoryAdapter);
            }
        }
    }

    @Nested
    @DisplayName("Tests para el método getCategory")
    class GetCategoryTests {

        @Test
        @DisplayName("Debe retornar la categoría correctamente para un TEACHER")
        void getCategory_SuccessTeacher() {
            Category savedCategory = Category.withTopicsAndStudents(1L, new Name("Math"), "Mathematics category", ownerDomain, List.of(), List.of());
            CategoryResponse categoryResponse = new CategoryResponse(1L, "Math", "Mathematics category", ownerIdStr, List.of(), List.of());

            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                authUtils.when(AuthenticationUtils::getUserId).thenReturn(ownerUuid);
                when(categoryRepositoryAdapter.findById(1L)).thenReturn(savedCategory);
                when(topicRepositoryAdapter.findByCategory(1L)).thenReturn(List.of());
                when(topicResponseMapper.toTopicResponseList(any())).thenReturn(List.of());
                when(userResponseMapper.toStudentResponseList(any())).thenReturn(List.of());
                when(categoryResponseMapper.toCategoryResponse(any(), any(), any())).thenReturn(categoryResponse);

                Result<CategoryResponse, Integer> result = categoryService.getCategory(1L);

                assertFalse(result.isFailure());
                assertEquals(1L, result.getValue().id());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si la categoría no existe")
        void getCategory_NotFound() {
            when(categoryRepositoryAdapter.findById(99L))
                    .thenThrow(new NotFoundException("Category not found", ErrorCode.CATEGORY_NOT_FOUND));

            Result<CategoryResponse, Integer> result = categoryService.getCategory(99L);

            assertTrue(result.isFailure());
            assertEquals(ErrorCode.CATEGORY_NOT_FOUND, result.getError());

        }
    }

    @Nested
    @DisplayName("Tests para el método deleteCategory")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Debe eliminar la categoría correctamente")
        void deleteCategory_Success() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.existsById(1L)).thenReturn(true);
                when(categoryRepositoryAdapter.countTopicList(1L)).thenReturn(0);

                Result<Void, Integer> result = categoryService.deleteCategory(1L);

                assertFalse(result.isFailure());
                verify(categoryRepositoryAdapter).delete(1L);
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si el usuario no es TEACHER")
        void deleteCategory_NotTeacher() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(false);

                Result<Void, Integer> result = categoryService.deleteCategory(1L);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.USER_NOT_TEACHER, result.getError());
                verify(categoryRepositoryAdapter, never()).delete(any());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si la categoría no existe")
        void deleteCategory_NotFound() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.existsById(99L)).thenReturn(false);

                Result<Void, Integer> result = categoryService.deleteCategory(99L);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_NOT_FOUND, result.getError());
                verify(categoryRepositoryAdapter, never()).delete(any());
            }
        }

        @Test
        @DisplayName("Debe retornar fallo si la categoría tiene tópicos")
        void deleteCategory_HasTopics() {
            try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
                authUtils.when(AuthenticationUtils::isTeacher).thenReturn(true);
                when(categoryRepositoryAdapter.existsById(1L)).thenReturn(true);
                when(categoryRepositoryAdapter.countTopicList(1L)).thenReturn(2);

                Result<Void, Integer> result = categoryService.deleteCategory(1L);

                assertTrue(result.isFailure());
                assertEquals(ErrorCode.CATEGORY_HAS_TOPICS, result.getError());
                verify(categoryRepositoryAdapter, never()).delete(any());
            }
        }
    }
}
