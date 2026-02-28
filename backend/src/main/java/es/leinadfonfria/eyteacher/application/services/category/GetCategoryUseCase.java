package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetCategoryUseCase {
    Result<GetCategoryResponse, Integer> getCategory(Long categoryId);
}
