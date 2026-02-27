package es.leinadfonfria.eyteacher.application.services.category;

import es.leinadfonfria.eyteacher.application.dtos.category.GetCategoryResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

import java.util.List;

public interface GetCategoriesByOwnerUseCase {
    Result<List<GetCategoryResponse>, Integer> getCategoriesByOwner(String ownerId);
}
