package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

/**
 * Use case for retrieving all students subscribed to topics belonging to a teacher's categories.
 */
public interface GetStudentsByOwnerIdUseCase {

    /**
     * Retrieves the distinct list of students enrolled in any topic whose category is owned by the given teacher.
     *
     * @param ownerId The string representation of the teacher's UUID.
     * @return Result containing the list of {@link UserResponse} students, or an error code on failure.
     */
    Result<List<UserResponse>, Integer> getStudentsByOwnerId(String ownerId);
}
