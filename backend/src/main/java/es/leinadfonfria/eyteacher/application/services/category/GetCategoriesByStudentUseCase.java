package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

/**
 * Use case to retrieve all categories related to a student through their subscribed topics.
 */
public interface GetCategoriesByStudentUseCase {
    Result<List<GetCategoryResponse>, Integer> getCategoriesByStudent(String studentId);
}
