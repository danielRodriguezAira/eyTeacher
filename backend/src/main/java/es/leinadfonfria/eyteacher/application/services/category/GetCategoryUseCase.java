package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetCategoryUseCase {
    Result<CategoryResponse, Integer> getCategory(Long categoryId);
}
