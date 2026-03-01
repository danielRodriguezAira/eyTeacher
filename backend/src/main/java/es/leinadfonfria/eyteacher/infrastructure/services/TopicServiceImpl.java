package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.services.topic.DeleteTopicUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.GetTopicUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.GetTopicsByOwnerAndCategoryUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.SaveTopicUseCase;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.CategoryMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TopicMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.UserMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of topic-related use cases.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements SaveTopicUseCase, GetTopicUseCase, GetTopicsByOwnerAndCategoryUseCase, DeleteTopicUseCase {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final TopicMapper topicMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Result<Long, Integer> saveTopic(SaveTopicRequest request) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("Topic category owner is not a TEACHER", ErrorCode.TOPIC_OWNER_NOT_TEACHER);
            }

            CategoryJpaEntity categoryEntity = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Topic category not found", ErrorCode.TOPIC_CATEGORY_NOT_FOUND));

            Topic topic;
            if (request.id() == null) {
                topic = Topic.create(
                        new Name(request.name()),
                        request.description(),
                        categoryMapper.toDomain(categoryEntity),
                        List.of()
                );
            } else {
                TopicJpaEntity existingTopic = topicRepository.findById(request.id())
                        .orElseThrow(() -> new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));

                topic = Topic.edit(
                        request.id(),
                        new Name(request.name()),
                        request.description(),
                        categoryMapper.toDomain(categoryEntity),
                        topicMapper.toDomain(existingTopic).getStudentList()
                );
            }

            TopicJpaEntity topicJpaEntity = topicRepository.save(topicMapper.toEntity(topic));
            return Result.ok(topicJpaEntity.getId());
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic creation/update", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public Result<GetTopicResponse, Integer> getTopic(Long id) {
        try {
            TopicJpaEntity topicEntity = topicRepository.findById(id)
                    .orElseThrow(() -> new AuthException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));
            Topic topic = topicMapper.toDomain(topicEntity);
            return Result.ok(new GetTopicResponse(
                    topic.getId(),
                    topic.getName().value(),
                    topic.getDescription(),
                    topic.getCategory().getId(),
                    userMapper.toStudentResponseList(topic.getStudentList())
            ));
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    //TODO Probablemente este método no sea necesario, ya que el listado de temas solo se necesita cuando se carga
    //el detalle de una categoría
    @Override
    public Result<List<GetTopicResponse>, Integer> getTopicsByCategory(String ownerId, Long categoryId) {
        try {
            CategoryJpaEntity categoryEntity = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AuthException("Topic category not found", ErrorCode.TOPIC_CATEGORY_NOT_FOUND));

            // Verify the owner of the category matches the requested ownerId
            if (!categoryEntity.getOwner().getId().toString().equals(ownerId)) {
                throw new AuthException("Category owner does not match requested owner", ErrorCode.CATEGORY_OWNER_NOT_FOUND);
            }

            List<GetTopicResponse> topics = topicRepository.findByCategory(categoryEntity).stream()
                    .map(topicMapper::toDomain)
                    .map(topic -> new GetTopicResponse(
                            topic.getId(),
                            topic.getName().value(),
                            topic.getDescription(),
                            topic.getCategory().getId(),
                            userMapper.toStudentResponseList(topic.getStudentList())
                    ))
                    .toList();

            return Result.ok(topics);
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topics retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public Result<Void, Integer> deleteTopic(Long id) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.TOPIC_OWNER_NOT_TEACHER);
            }
            if (!topicRepository.existsById(id)) {
                throw new AuthException("Topic not found", ErrorCode.TOPIC_NOT_FOUND);
            }
            topicRepository.deleteById(id);
            return Result.ok(null);
        } catch (AuthException e) {
            log.error("Authentication error during topic deletion", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic deletion", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
