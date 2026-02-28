package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface DeleteTopicUseCase {
    Result<Void, Integer> deleteTopic(Long id);
}
