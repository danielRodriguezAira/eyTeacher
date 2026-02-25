package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.UpdateUserProfileRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

/**
 * Interface for updating an existing user.
 */
public interface UpdateUserProfileUseCase {
    Result<Void, Integer> update(UpdateUserProfileRequest request);
}
