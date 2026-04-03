package es.leinadfonfria.eyteacher.application.dtos.correction;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;

import java.time.LocalDateTime;

public record CorrectionResponse(
        Long id,
        String description,
        UserResponse teacher,
        Long solutionId,
        LocalDateTime createdAt
) {}
