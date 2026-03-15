package es.leinadfonfria.eyteacher.application.services.task;

public record SaveTaskRequest(
        Long id,
        String description,
        Long topicId
) {}
