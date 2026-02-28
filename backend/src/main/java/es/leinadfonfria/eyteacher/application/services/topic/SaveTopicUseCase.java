package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface SaveTopicUseCase {
    Result<Long, Integer> saveTopic(SaveTopicRequest request);
}
