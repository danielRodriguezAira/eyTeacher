package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a Topic in the system.
 * This is a domain entity that holds core topic information and business rules.
 */
@Getter
@Builder
public class Topic {
    private final Long id;
    private final Name name;
    private final String description;
    private final Category category;
    private final List<User> studentList;
    private final List<Task> taskList;

    private Topic(Long id, Name name, String description, Category category, List<User> studentList, List<Task> taskList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.studentList = studentList != null ? studentList : Collections.emptyList();
        this.taskList = taskList != null ? taskList : Collections.emptyList();
    }

    /**
     * Creates a new Topic instance (for creation).
     *
     * @param name        The topic firstName.
     * @param description The topic description.
     * @param category    The category this topic belongs to.
     * @param studentList The list of students subscribed to this topic.
     * @return Topic A new topic domain entity.
     */
    public static Topic create(Name name, String description, Category category, List<User> studentList) {
        return new Topic(null, name, description, category, studentList, Collections.emptyList());
    }

    /**
     * Edits a Topic instance (for update).
     *
     * @param id          The unique identifier of the topic.
     * @param name        The topic firstName.
     * @param description The topic description.
     * @param category    The category this topic belongs to.
     * @param studentList The list of students subscribed to this topic.
     * @param taskList    The list of tasks in this topic.
     * @return Topic A new topic domain entity.
     */
    public static Topic edit(Long id, Name name, String description, Category category, List<User> studentList, List<Task> taskList) {
        return new Topic(id, name, description, category, studentList, taskList);
    }
}
