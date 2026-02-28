package es.leinadfonfria.eyteacher.domain.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    private Category(String name, String description, User owner) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    private Category(Long id, String name, String description, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
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
        return new Category(name, description, owner);
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
        return new Category(id, name, description, owner);
    }
}
