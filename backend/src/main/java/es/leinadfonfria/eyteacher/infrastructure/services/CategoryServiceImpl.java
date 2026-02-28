package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.services.category.DeleteCategoryUseCase;
import es.leinadfonfria.eyteacher.application.services.category.GetCategoriesByOwnerUseCase;
import es.leinadfonfria.eyteacher.application.services.category.GetCategoryUseCase;
import es.leinadfonfria.eyteacher.application.services.category.SaveCategoryUseCase;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.CategoryMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TopicMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.UserRepository;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of category-related use cases.
 * Orchestrates category creation using repositories and mappers.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements SaveCategoryUseCase, GetCategoriesByOwnerUseCase, GetCategoryUseCase, DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final TopicMapper topicMapper;

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
                throw new AuthException("Category owner is not a TEACHER", ErrorCode.CATEGORY_OWNER_NOT_TEACHER);
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
                        request.name(),
                        request.description(),
                        userMapper.toDomain(ownerEntity)
                );
            } else {
                category = Category.edit(
                        request.id(),
                        request.name(),
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
                    .orElseThrow(() -> new AuthException("Category not found", ErrorCode.CATEGORY_NOT_FOUND));

            Category category = Category.withTopics(
                    categoryEntity.getId(),
                    categoryEntity.getName(),
                    categoryEntity.getDescription(),
                    userMapper.toDomain(categoryEntity.getOwner()),
                    topicMapper.toDomainList(categoryEntity.getTopicList())
            );

            List<GetTopicResponse> topicResponseList = category.getTopicList().stream()
                    .map(topic -> new GetTopicResponse(
                            topic.getId(),
                            topic.getName(),
                            topic.getDescription(),
                            category.getId()
                    ))
                    .toList();

            return Result.ok(new GetCategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    category.getOwner().getId().value().toString(),
                    topicResponseList
            ));
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

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
                    .orElseThrow(() -> new AuthException("Category owner not found", ErrorCode.CATEGORY_OWNER_NOT_FOUND));

            List<GetCategoryResponse> categories = categoryRepository.findByOwner(ownerEntity).stream()
                    .map(categoryEntity -> new GetCategoryResponse(
                            categoryEntity.getId(),
                            categoryEntity.getName(),
                            categoryEntity.getDescription(),
                            categoryEntity.getOwner().getId().toString(),
                            Collections.emptyList()
                    ))
                    .toList();

            return Result.ok(categories);
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
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
                throw new AuthException("User is not a TEACHER", ErrorCode.CATEGORY_OWNER_NOT_TEACHER);
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
}
