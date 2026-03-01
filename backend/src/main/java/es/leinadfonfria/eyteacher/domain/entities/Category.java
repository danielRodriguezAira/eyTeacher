package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
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
    private final Name name;
    private final String description;
    private final User owner;
    private final List<Topic> topicList;
    private final List<User> studentList;


    private Category(Long id, Name name, String description, User owner, List<Topic> topicList, List<User> studentList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.topicList = topicList != null ? topicList : Collections.emptyList();
        this.studentList = studentList != null ? studentList : Collections.emptyList();
    }

    /**
     * Creates a new Category instance.
     *
     * @param name        The category firstName.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @return Category A new category domain entity.
     */
    public static Category create(Name name, String description, User owner) {
        return new Category(null, name, description, owner, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Edits a Category instance.
     *
     * @param id          The unique identifier of the category.
     * @param name        The category firstName.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @return Category A new category domain entity.
     */
    public static Category edit(Long id, Name name, String description, User owner) {
        return new Category(id, name, description, owner, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Recreates a Category instance with its topics (usually from persistence).
     *
     * @param id          The unique identifier of the category.
     * @param name        The category firstName.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @param topicList   The list of topics in this category.
     * @return Category A new category domain entity.
     */
    public static Category withTopics(Long id, Name name, String description, User owner, List<Topic> topicList) {
        return new Category(id, name, description, owner, topicList, Collections.emptyList());
    }

    /**
     * Recreates a Category instance with its topics and students (usually from persistence).
     *
     * @param id          The unique identifier of the category.
     * @param name        The category firstName.
     * @param description The category description.
     * @param owner       The user who owns this category.
     * @param topicList   The list of topics in this category.
     * @return Category A new category domain entity.
     */
    public static Category withTopicsAndStudents(Long id, Name name, String description, User owner, List<Topic> topicList, List<User> studentList) {
        return new Category(id, name, description, owner, topicList, studentList);
    }
}
