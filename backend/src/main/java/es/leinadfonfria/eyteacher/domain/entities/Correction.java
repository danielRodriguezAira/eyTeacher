package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents a Correction for a Solution.
 */
@Getter
@Builder
public class Correction {
    private final Long id;
    private final String description;
    private final User teacher;
    private final Long solutionId;
    private final LocalDateTime createdAt;

    private Correction(Long id, String description, User teacher, Long solutionId, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.teacher = teacher;
        this.solutionId = solutionId;
        this.createdAt = createdAt;
    }

    public static Correction create(String description, User teacher, Long solutionId) {
        return new Correction(null, description, teacher, solutionId, null);
    }
}
