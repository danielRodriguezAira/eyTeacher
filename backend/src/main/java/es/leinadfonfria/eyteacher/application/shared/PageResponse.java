package es.leinadfonfria.eyteacher.application.shared;

import java.util.List;

/**
 * Application-layer DTO for paginated API responses.
 * Wraps a page of items together with pagination metadata for consumers.
 *
 * @param <T>     The type of item in the page.
 * @param content The list of items for the current page.
 * @param page    The zero-based page number that was requested.
 * @param size    The page size that was requested.
 * @param hasNext True if at least one more record exists beyond this page.
 */
public record PageResponse<T>(List<T> content, int page, int size, boolean hasNext) {}
