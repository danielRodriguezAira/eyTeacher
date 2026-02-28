package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface DeleteCategoryUseCase {
    Result<Void, Integer> deleteCategory(Long categoryId);
}
