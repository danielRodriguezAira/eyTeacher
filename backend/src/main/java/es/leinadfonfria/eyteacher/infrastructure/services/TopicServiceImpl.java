package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.topic.AddTopicSubscriptionToStudentsRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponseMapper;
import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationRequest;
import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.*;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.*;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.CategoryRepository;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.infrastructure.events.NotificationPublisher;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewSubscriptionMessage;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters.TopicRepositoryAdapter;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of topic-related use cases.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements SaveTopicUseCase, GetTopicUseCase, GetTopicsByOwnerAndCategoryUseCase, DeleteTopicUseCase, AddTopicSubscriptionToStudentsUseCase {

    private final TopicRepositoryAdapter topicRepository;
    private final CategoryRepository<Category> categoryRepository;
    private final TaskRepository<Task> taskRepository;
    private final UserRepository<User> userRepository;
    private final TopicResponseMapper topicResponseMapper;
    private final NotificationPublisher notificationPublisher;

    /**
     * Saves a new topic or updates an existing one.
     * @param request The topic save request containing topic details.
     * @return Result containing the topic ID or an error code.
     */
    @Override
    @Transactional
    public Result<Long, Integer> saveTopic(SaveTopicRequest request) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("Topic category owner is not a TEACHER", ErrorCode.TOPIC_OWNER_NOT_TEACHER);
            }

            Category category = categoryRepository.findById(request.categoryId());

            Topic topic;
            if (request.id() == null) {
                topic = Topic.create(
                        new Name(request.name()),
                        request.description(),
                        category,
                        List.of()
                );
            } else {
                Topic existingTopic = topicRepository.findById(request.id());
                topic = Topic.edit(
                        request.id(),
                        new Name(request.name()),
                        request.description(),
                        category,
                        existingTopic.getStudentList(),
                        existingTopic.getTaskList()
                );
            }

            Topic saved = topicRepository.save(topic);
            return Result.ok(saved.getId());
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

    /**
     * Retrieves a topic by its ID.
     * @param id The ID of the topic to retrieve.
     * @return Result containing the topic response or an error code.
     */
    @Override
    public Result<TopicResponse, Integer> getTopic(Long id) {
        try {
            Topic topic = topicRepository.findById(id);
            List<Task> taskList = taskRepository.findByTopicId(id);
            return Result.ok(topicResponseMapper.toTopicResponse(topic, taskList));
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    //TODO Probablemente este método no sea necesario, ya que el listado de temas solo se necesita cuando se carga
    //el detalle de una categoría
    @Override
    public Result<List<TopicResponse>, Integer> getTopicsByCategory(String ownerId, Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId);

            if (!category.getOwner().getId().value().toString().equals(ownerId)) {
                throw new AuthException("Category owner does not match requested owner", ErrorCode.CATEGORY_USER_NOT_FOUND);
            }

            List<Topic> topics = topicRepository.findByCategory(categoryId);
            return Result.ok(topicResponseMapper.toTopicResponseList(topics));
        } catch (AuthException e) {
            log.error("Authentication error", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topics retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Deletes a topic by its ID.
     * @param id The ID of the topic to delete.
     * @return Result indicating success or failure.
     */
    @Override
    @Transactional
    public Result<Void, Integer> deleteTopic(Long id) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.TOPIC_OWNER_NOT_TEACHER);
            }
            if (!topicRepository.existsById(id)) {
                throw new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND);
            }
            topicRepository.delete(id);
            return Result.ok(null);
        } catch (AuthException e) {
            log.error("Authentication error during topic deletion", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during topic deletion", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic deletion", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Adds a list of students to a topic.
     * @param request The request containing topic ID and student IDs.
     */
    @Override
    @Transactional
    public Result<Void, Integer> addTopicSubscriptionToStudents(AddTopicSubscriptionToStudentsRequest request) {
        try {
            Topic topic = topicRepository.findById(request.topicId());

            if (!topic.getCategory().getOwner().getId().value().equals(AuthenticationUtils.getUserId())) {
                throw new AuthException("Authenticated user is not the owner of the topic category", ErrorCode.AUTHENTICATION_ERROR);
            }

            List<User> currentStudents = new ArrayList<>(topic.getStudentList());
            List<User> newStudents = new ArrayList<>();
            for (String email : request.studentEmailList()) {
                Optional<User> studentOpt = userRepository.findByEmail(email);
                if (studentOpt.isEmpty()) {
                    log.warn("Student not found by email: {}", email);
                    continue;
                }
                User student = studentOpt.get();
                if (!currentStudents.contains(student)) {
                    currentStudents.add(student);
                    newStudents.add(student);
                }
            }

            Topic updatedTopic = Topic.edit(
                    topic.getId(),
                    topic.getName(),
                    topic.getDescription(),
                    topic.getCategory(),
                    currentStudents,
                    topic.getTaskList()
            );

            topicRepository.save(updatedTopic);
            List<java.util.UUID> studentIds = newStudents.stream()
                    .map(s -> s.getId().value())
                    .toList();
            notificationPublisher.publishNewSubscription(new NewSubscriptionMessage(
                    topic.getId(),
                    topic.getName().value(),
                    topic.getCategory().getOwner().getFullName(),
                    studentIds
            ));
            return Result.ok(null);

        } catch (AuthException e) {
            log.error("Authentication error during topic subscription", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Topic not found during subscription", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during topic subscription", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
