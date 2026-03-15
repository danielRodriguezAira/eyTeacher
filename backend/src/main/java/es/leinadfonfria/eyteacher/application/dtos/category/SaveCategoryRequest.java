package es.leinadfonfria.eyteacher.application.dtos.category;

/**
 * Data transfer object for editing and adding a new category.
 *
 * @param id          The category id. null if new category.
 * @param name        The category firstName.
 * @param description The category description.
 * @param ownerId     The UUID string of the user who owns this category.
 */
public record SaveCategoryRequest(
        Long id,
        String name,
        String description,
        String ownerId
) {
    public SaveCategoryRequest(String name, String description, String ownerId) {
        this(null, name, description, ownerId);
    }
}
