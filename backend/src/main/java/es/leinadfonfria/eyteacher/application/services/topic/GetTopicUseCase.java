package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetTopicUseCase {
    Result<TopicResponse, Integer> getTopic(Long id);
}
