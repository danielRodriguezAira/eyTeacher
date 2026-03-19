package es.leinadfonfria.eyteacher.application.services.correction;

public record AddCorrectionRequest(
        String description,
        Long solutionId
) {}
