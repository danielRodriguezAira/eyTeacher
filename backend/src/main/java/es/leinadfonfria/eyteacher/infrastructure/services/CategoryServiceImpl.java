package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponse;
import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponseMapper;
import es.leinadfonfria.eyteacher.application.services.category.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.CategoryRepository;
import es.leinadfonfria.eyteacher.domain.ports.TopicRepository;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of category-related use cases.
 * Orchestrates category creation using repositories and mappers.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements SaveCategoryUseCase, GetCategoriesByOwnerUseCase, GetCategoriesByStudentUseCase, GetCategoryUseCase, DeleteCategoryUseCase {

    private final CategoryRepository<Category> categoryRepository;
    private final TopicRepository<Topic> topicRepository;
    private final UserRepository<User> userRepository;
    private final CategoryResponseMapper categoryResponseMapper;
    private final UserResponseMapper userResponseMapper;
    private final TopicResponseMapper topicResponseMapper;

    /**
     * Adds a new category to the system. If the category already exists, it updates it.
     *
     * @param request The category creation details.
     * @return Result<Long, Integer> Success with the category ID or an error code.
     */
    @Override
    @Transactional
    public Result<Long, Integer> saveCategory(SaveCategoryRequest request) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("Authenticated user is not a TEACHER", ErrorCode.USER_NOT_TEACHER);
            }
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(request.ownerId());
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid owner ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }

            User owner = userRepository.findById(ownerUuid)
                    .orElseThrow(() -> new AuthException("Category owner not found", ErrorCode.CATEGORY_USER_NOT_FOUND));

            Category category;
            Category updatedCategory;
            if (request.id() == null) {
                category = Category.create(
                        new Name(request.name()),
                        request.description(),
                        owner
                );
                updatedCategory = categoryRepository.save(category);
            } else {
                category = Category.edit(
                        request.id(),
                        new Name(request.name()),
                        request.description()
                );
                updatedCategory = categoryRepository.update(category);
            }
            return Result.ok(updatedCategory.getId());
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category creation", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves all data of a category by its ID.
     *
     * @param categoryId The ID of the category.
     * @return Result<CategoryResponse, Integer> Success with the category data or an error code.
     */
    @Override
    public Result<CategoryResponse, Integer> getCategory(Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId);

            validateUserHasPermissions(category);

            List<Topic> topicList;
            List<UserResponse> studentList;
            if (AuthenticationUtils.isTeacher()) {
                topicList = topicRepository.findByCategory(categoryId);
                studentList = userResponseMapper.toStudentResponseList(category.getStudentList());
            } else {
                topicList = topicRepository.findByCategoryAndStudent(categoryId, AuthenticationUtils.getUserId());
                studentList = List.of();
            }
            List<TopicResponse> topicResponseList = topicResponseMapper.toTopicResponseList(topicList);
            return Result.ok(categoryResponseMapper.toCategoryResponse(
                    category,
                    topicResponseList,
                    studentList)
            );
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves categories owned by a specific user.
     *
     * @param ownerId The ID of the user whose categories are to be retrieved.
     * @return A Result containing a list of CategoryResponse objects or an error code.
     */
    @Override
    public Result<List<CategoryResponse>, Integer> getCategoriesByOwner(String ownerId) {
        try {
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(ownerId);
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid owner ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }
            if (userRepository.findById(ownerUuid).isEmpty()) {
                throw new NotFoundException("Category owner not found", ErrorCode.CATEGORY_USER_NOT_FOUND);
            }

            List<Category> categoryList = categoryRepository.findByOwnerId(new UserId(ownerUuid));
            List<CategoryResponse> categories = categoryResponseMapper.toCategoryResponseList(categoryList);

            return Result.ok(categories);
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves categories in which a specific student is enrolled.
     *
     * @param studentId The ID of the student whose categories are to be retrieved.
     * @return A Result containing a list of CategoryResponse objects or an error code.
     */
    @Override
    public Result<List<CategoryResponse>, Integer> getCategoriesByStudent(String studentId) {
        try {
            if (!AuthenticationUtils.isStudent()) {
                throw new AuthException("Authenticated user is not a STUDENT", ErrorCode.USER_NOT_STUDENT);
            }
            UUID userUuid;
            try {
                userUuid = UUID.fromString(studentId);
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid user ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }
            List<Category> categoryList = categoryRepository.findByStudentId(new UserId(userUuid));
            List<CategoryResponse> categoryResponseList = categoryResponseMapper.toCategoryResponseList(categoryList);

            return Result.ok(categoryResponseList);
        } catch (AuthException e) {
            log.error("Authentication error during category retrieval for student", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval for student", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId The ID of the category to delete.
     * @return Result<Void, Integer> Success or an error code.
     */
    @Override
    @Transactional
    public Result<Void, Integer> deleteCategory(Long categoryId) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.USER_NOT_TEACHER);
            }
            if (!categoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Category not found", ErrorCode.CATEGORY_NOT_FOUND);
            }
            if (topicRepository.existsByCategoryId(categoryId)) {
                throw new NotFoundException("Category has topics, cannot be deleted", ErrorCode.CATEGORY_HAS_TOPICS);
            }
            categoryRepository.delete(categoryId);
            return Result.ok(null);
        } catch (AuthException e) {
            log.error("Authentication error during category deletion", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category deletion", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    private void validateUserHasPermissions(Category category) {
        UUID authUserId = AuthenticationUtils.getUserId();
        if (AuthenticationUtils.isTeacher()) {
            if (!category.getOwner().getId().value().equals(authUserId)) {
                throw new AuthException("Authenticated TEACHER is not the owner of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        } else if (AuthenticationUtils.isStudent()) {
            boolean isEnrolled = category.getStudentList().stream()
                    .anyMatch(student -> student.getId().value().equals(authUserId));
            if (!isEnrolled) {
                throw new AuthException("Authenticated STUDENT is not enrolled in any topic of this category", ErrorCode.AUTHENTICATION_ERROR);
            }
        }
    }
}
