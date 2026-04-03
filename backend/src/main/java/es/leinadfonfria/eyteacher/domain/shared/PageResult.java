package es.leinadfonfria.eyteacher.domain.shared;

import java.util.List;

/**
 * Domain-layer pagination result.
 * Contains the page content and a flag indicating whether more records exist.
 * Intentionally free of any framework dependency.
 *
 * @param <T> The type of domain object contained in this page.
 * @param content  The list of items for the current page.
 * @param hasNext  True if at least one more record exists beyond this page.
 */
public record PageResult<T>(List<T> content, boolean hasNext) {}
