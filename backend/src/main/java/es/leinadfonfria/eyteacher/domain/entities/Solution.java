package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a Solution to a Task.
 */
@Getter
@Builder
public class Solution {
    private final Long id;
    private final String description;
    private final User student;
    private final Task task;
    private final Correction correction;

    private Solution(Long id, String description, User student, Task task, Correction correction) {
        this.id = id;
        this.description = description;
        this.student = student;
        this.task = task;
        this.correction = correction;
    }

    public static Solution create(String description, User student, Task task) {
        return new Solution(null, description, student, task, null);
    }
}
