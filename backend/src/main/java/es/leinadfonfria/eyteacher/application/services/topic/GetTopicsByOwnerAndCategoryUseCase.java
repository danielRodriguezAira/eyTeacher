package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetTopicsByOwnerAndCategoryUseCase {
    Result<List<GetTopicResponse>, Integer> getTopicsByCategory(String ownerId, Long categoryId);
}
