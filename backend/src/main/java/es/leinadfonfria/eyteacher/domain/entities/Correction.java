package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

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

    private Correction(Long id, String description, User teacher, Long solutionId) {
        this.id = id;
        this.description = description;
        this.teacher = teacher;
        this.solutionId = solutionId;
    }

    public static Correction create(String description, User teacher, Long solutionId) {
        return new Correction(null, description, teacher, solutionId);
    }
}
