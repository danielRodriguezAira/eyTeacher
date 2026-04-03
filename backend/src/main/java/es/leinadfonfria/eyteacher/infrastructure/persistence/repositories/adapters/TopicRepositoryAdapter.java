package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.TopicRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.TopicMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryJpaRepository;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.TopicJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepository<Topic> {

    private final TopicJpaRepository topicJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TopicMapper topicMapper;

    @Override
    public Topic findById(Long id) {
        return topicJpaRepository.findById(id)
                .map(topicMapper::toDomain)
                .orElseThrow(() -> new NotFoundException("Topic not found", ErrorCode.TOPIC_NOT_FOUND));
    }

    @Override
    public List<Topic> findByCategory(Long categoryId) {
        CategoryJpaEntity categoryEntity = categoryJpaRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.TOPIC_CATEGORY_NOT_FOUND));
        return topicMapper.toDomainList(topicJpaRepository.findByCategoryId(categoryEntity.getId()));
    }

    @Override
    public List<Topic> findByCategoryAndStudent(Long categoryId, UUID studentId) {
        return topicMapper.toDomainList(
                topicJpaRepository.findTopicByCategoryIdAndStudentId(categoryId, studentId));
    }

    @Override
    public Topic save(Topic topic) {
        return topicMapper.toDomain(
                topicJpaRepository.save(topicMapper.toEntity(topic)));
    }

    @Override
    public boolean existsById(Long id) {
        return topicJpaRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        topicJpaRepository.deleteById(id);
    }
}
