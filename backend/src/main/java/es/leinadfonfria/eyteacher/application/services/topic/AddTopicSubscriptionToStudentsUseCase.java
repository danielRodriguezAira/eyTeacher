package es.leinadfonfria.eyteacher.application.services.topic;

import es.leinadfonfria.eyteacher.application.dtos.topic.AddTopicSubscriptionToStudentsRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface AddTopicSubscriptionToStudentsUseCase {
    Result<Void, Integer> addTopicSubscriptionToStudents(AddTopicSubscriptionToStudentsRequest request);
}
