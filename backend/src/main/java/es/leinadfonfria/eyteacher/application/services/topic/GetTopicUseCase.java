package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.GetTopicResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetTopicUseCase {
    Result<GetTopicResponse, Integer> getTopic(Long id);
}
