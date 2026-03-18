package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.CategoryResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetCategoriesByOwnerUseCase {
    Result<List<CategoryResponse>, Integer> getCategoriesByOwner(String ownerId);
}
