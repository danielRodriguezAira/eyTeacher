package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.services.category.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
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
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of category-related use cases.
 * Orchestrates category creation using repositories and mappers.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements SaveCategoryUseCase, GetCategoriesByOwnerUseCase, GetCategoriesByStudentUseCase, GetCategoryUseCase, DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final TopicMapper topicMapper;
    private final TopicRepository topicRepository;

    /**
     * Adds a new category to the system. If the category already exists, it updates it.
     *
     * @param request The category creation details.
     * @return Result<Void, Integer> Success or an error code.
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
            UserJpaEntity ownerEntity = userRepository.findById(ownerUuid)
                    .orElseThrow(() -> new AuthException("Category owner not found", ErrorCode.CATEGORY_OWNER_NOT_FOUND));

            Category category;
            if(request.id() == null) {
                category = Category.create(
                        new Name(request.name()),
                        request.description(),
                        userMapper.toDomain(ownerEntity)
                );
            } else {
                category = Category.edit(
                        request.id(),
                        new Name(request.name()),
                        request.description(),
                        userMapper.toDomain(ownerEntity)
                );
            }

            CategoryJpaEntity categoryJpaEntity = categoryRepository.save(categoryMapper.toEntity(category));
            return Result.ok(categoryJpaEntity.getId());
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
     * @return Result<GetCategoryResponse, Integer> Success with the category data or an error code.
     */
    @Override
    public Result<GetCategoryResponse, Integer> getCategory(Long categoryId) {
        try {
            CategoryJpaEntity categoryEntity = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_NOT_FOUND));

            UUID authUserId = AuthenticationUtils.getUserId();
            if (AuthenticationUtils.isTeacher()) {
                if (!categoryEntity.getOwner().getId().equals(authUserId)) {
                    throw new AuthException("Authenticated TEACHER is not the owner of this category", ErrorCode.AUTHENTICATION_ERROR);
                }
            } else if (AuthenticationUtils.isStudent()) {
                boolean isEnrolled = categoryEntity.getStudentList().stream()
                        .anyMatch(student -> student.getId().equals(authUserId));
                if (!isEnrolled) {
                    throw new AuthException("Authenticated STUDENT is not enrolled in any topic of this category", ErrorCode.AUTHENTICATION_ERROR);
                }
            }

            Category category = Category.withTopicsAndStudents(
                    categoryEntity.getId(),
                    new Name(categoryEntity.getName()),
                    categoryEntity.getDescription(),
                    userMapper.toDomain(categoryEntity.getOwner()),
                    getTopicJpaEntities(categoryId, categoryEntity),
                    getStudentList(categoryEntity)
            );

            List<GetTopicResponse> topicResponseList = category.getTopicList().stream()
                    .map(topic -> new GetTopicResponse(
                            topic.getId(),
                            topic.getName().value(),
                            topic.getDescription(),
                            category.getId(),
                            List.of()
                    ))
                    .toList();

            return Result.ok(new GetCategoryResponse(
                    category.getId(),
                    category.getName().value(),
                    category.getDescription(),
                    category.getOwner().getId().value().toString(),
                    topicResponseList,
                    category.getStudentList().stream()
                            .map(userMapper::toStudentResponse)
                            .toList()
            ));
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
     * @param ownerId The ID of the user whose categories are to be retrieved.
     * @return A Result containing a list of GetCategoryResponse objects or an error code.
     */
    @Override
    public Result<List<GetCategoryResponse>, Integer> getCategoriesByOwner(String ownerId) {
        try {
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(ownerId);
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid owner ID format", e, ErrorCode.INVALID_USER_ID_FORMAT);
            }
            UserJpaEntity ownerEntity = userRepository.findById(ownerUuid)
                    .orElseThrow(() -> new NotFoundException("Category owner not found", ErrorCode.CATEGORY_OWNER_NOT_FOUND));

            List<GetCategoryResponse> categories = categoryRepository.findByOwner(ownerEntity).stream()
                    .map(categoryEntity -> new GetCategoryResponse(
                            categoryEntity.getId(),
                            categoryEntity.getName(),
                            categoryEntity.getDescription(),
                            categoryEntity.getOwner().getId().toString(),
                            Collections.emptyList(),
                            Collections.emptyList()
                    ))
                    .toList();

            return Result.ok(categories);
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        }  catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves categories owned by a specific user.
     * @param studentId The ID of the student whose categories are to be retrieved.
     * @return A Result containing a list of GetCategoryResponse objects or an error code.
     */
    @Override
    public Result<List<GetCategoryResponse>, Integer> getCategoriesByStudent(String studentId) {
        try {
            if (!AuthenticationUtils.isStudent()) {
                throw new AuthException("Authenticated user is not a STUDENT", ErrorCode.USER_NOT_STUDENT);
            }
            UUID userUuid = UUID.fromString(studentId);
            List<GetCategoryResponse> categories = categoryRepository.findByStudentId(userUuid).stream()
                    .map(categoryEntity -> new GetCategoryResponse(
                            categoryEntity.getId(),
                            categoryEntity.getName(),
                            categoryEntity.getDescription(),
                            categoryEntity.getOwner().getId().toString(),
                            Collections.emptyList(),
                            Collections.emptyList()
                    ))
                    .toList();

            return Result.ok(categories);
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
                throw new AuthException("Category not found", ErrorCode.CATEGORY_NOT_FOUND);
            }
            categoryRepository.deleteById(categoryId);
            return Result.ok(null);
        } catch (AuthException e) {
            log.error("Authentication error during category deletion", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category deletion", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    private @NonNull List<User> getStudentList(CategoryJpaEntity categoryEntity) {
        return categoryEntity.getStudentList().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of TopicJpaEntity objects associated with a specific category.
     * Handles authorization based on the user's role and ensures the category owner matches
     * the authenticated user before retrieving the topics.
     *
     * @param categoryId the ID of the category for which topics are to be fetched.
     * @param categoryEntity the CategoryJpaEntity instance representing the category details, including its owner.
     * @return a list of TopicJpaEntity objects associated with the given category.
     * @throws AuthException if the authenticated user is not the owner of the specified category.
     */
    private List<Topic> getTopicJpaEntities(Long categoryId, CategoryJpaEntity categoryEntity) {
        List<TopicJpaEntity> topicList;
        if(AuthenticationUtils.isStudent()) {
            topicList = topicRepository.findTopicByCategoryIdAndStudentId(categoryId, AuthenticationUtils.getUserId());
        } else {
            topicList = categoryEntity.getTopicList();
        }
        return topicMapper.toDomainList(topicList);
    }
}
