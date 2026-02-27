package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface SaveCategoryUseCase {
    Result<Long, Integer> saveCategory(SaveCategoryRequest request);
}
