package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a Topic in the system.
 * This is a domain entity that holds core topic information and business rules.
 */
@Getter
@Builder
public class Topic {
    private final Long id;
    private final String name;
    private final String description;
    private final Category category;

    private Topic(Long id, String name, String description, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    /**
     * Creates a new Topic instance (for creation).
     *
     * @param name        The topic name.
     * @param description The topic description.
     * @param category    The category this topic belongs to.
     * @return Topic A new topic domain entity.
     */
    public static Topic create(String name, String description, Category category) {
        return new Topic(null, name, description, category);
    }

    /**
     * Edits a Topic instance (for update).
     *
     * @param id          The unique identifier of the topic.
     * @param name        The topic name.
     * @param description The topic description.
     * @param category    The category this topic belongs to.
     * @return Topic A new topic domain entity.
     */
    public static Topic edit(Long id, String name, String description, Category category) {
        return new Topic(id, name, description, category);
    }
}
