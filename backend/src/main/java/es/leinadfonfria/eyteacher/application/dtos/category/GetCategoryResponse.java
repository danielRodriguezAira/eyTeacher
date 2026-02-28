package es.leinadfonfria.eyteacher.application.dtos.category;

/**
 * Data transfer object for returning category data.
 *
 * @param id          The category id.
 * @param name        The category name.
 * @param description The category description.
 * @param ownerId     The UUID string of the user who owns this category.
 */
public record GetCategoryResponse(
        Long id,
        String name,
        String description,
        String ownerId
) {
}
