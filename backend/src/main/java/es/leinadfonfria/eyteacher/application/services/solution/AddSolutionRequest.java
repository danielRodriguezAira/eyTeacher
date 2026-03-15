package es.leinadfonfria.eyteacher.application.services.solution;

public record AddSolutionRequest(
        String description,
        Long taskId
) {}
