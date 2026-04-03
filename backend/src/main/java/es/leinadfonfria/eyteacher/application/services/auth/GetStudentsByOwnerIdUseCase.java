package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.shared.PageResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

/**
 * Use case for retrieving students subscribed to topics belonging to a teacher's categories.
 */
public interface GetStudentsByOwnerIdUseCase {

    /**
     * Retrieves a page of distinct students enrolled in any topic whose category is owned by the given teacher.
     *
     * @param ownerId The string representation of the teacher's UUID.
     * @param page    Zero-based page number.
     * @param size    Maximum number of items per page.
     * @return Result containing a {@link PageResponse} of {@link UserResponse} students, or an error code on failure.
     */
    Result<PageResponse<UserResponse>, Integer> getStudentsByOwnerId(String ownerId, int page, int size);
}
