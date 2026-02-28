package es.leinadfonfria.eyteacher.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a Category in the system.
 * This is a domain entity that holds core category information and business rules.
 */
@Getter
@Builder
public class Category {
    private final Long id;
    private final String name;
    private final String description;
    private final User owner;
    private final List<Topic> topicList;


    private Category(Long id, String name, String description, User owner, List<Topic> topicList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.topicList = topicList != null ? topicList : Collections.emptyList();
    }

    /**
     * Creates a new Category instance.
     *
     * @param name        The category name.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @return Category A new category domain entity.
     */
    public static Category create(String name, String description, User owner) {
        return new Category(null, name, description, owner, Collections.emptyList());
    }

    /**
     * Edits a Category instance.
     *
     * @param id          The unique identifier of the category.
     * @param name        The category name.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @return Category A new category domain entity.
     */
    public static Category edit(Long id, String name, String description, User owner) {
        return new Category(id, name, description, owner, Collections.emptyList());
    }

    /**
     * Recreates a Category instance with its topics (usually from persistence).
     *
     * @param id          The unique identifier of the category.
     * @param name        The category name.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @param topicList   The list of topics in this category.
     * @return Category A new category domain entity.
     */
    public static Category withTopics(Long id, String name, String description, User owner, List<Topic> topicList) {
        return new Category(id, name, description, owner, topicList);
    }
}
