package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private final LocalDateTime createdAt;

    private Solution(Long id, String description, User student, Task task, Correction correction, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.student = student;
        this.task = task;
        this.correction = correction;
        this.createdAt = createdAt;
    }

    public static Solution create(String description, User student, Task task) {
        return new Solution(null, description, student, task, null, null);
    }
}
