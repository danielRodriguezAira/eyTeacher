package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetTopicsByOwnerAndCategoryUseCase {
    Result<List<TopicResponse>, Integer> getTopicsByCategory(String ownerId, Long categoryId);
}
