/**
 * Generic paginated response returned by the API for list endpoints.
 */
export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    hasNext: boolean;
}
