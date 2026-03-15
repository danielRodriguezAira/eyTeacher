package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a Task in the system.
 */
@Getter
@Builder
public class Task {
    private final Long id;
    private final String description;
    private final Topic topic;
    private final List<Solution> solutionList;

    private Task(Long id, String description, Topic topic, List<Solution> solutionList) {
        this.id = id;
        this.description = description;
        this.topic = topic;
        this.solutionList = solutionList != null ? solutionList : Collections.emptyList();
    }

    public static Task create(String description) {
        return new Task(null, description, null, Collections.emptyList());
    }

    public static Task create(String description, Topic topic) {
        return new Task(null, description, topic, Collections.emptyList());
    }

    public static Task create(Long id, String description, Topic topic, List<Solution> solutionList) {
        return new Task(id, description, topic, solutionList);
    }

    public static Task edit(Long id, String description) {
        return new Task(id, description, null, Collections.emptyList());
    }
}
