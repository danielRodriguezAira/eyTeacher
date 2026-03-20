package es.leinadfonfria.eyteacher.application.dtos.topic;

import java.util.List;
import java.util.UUID;

public record AddTopicSubscriptionToStudentsRequest(
        Long topicId,
        UUID ownerId,
        List<String> studentEmailList
) {}
