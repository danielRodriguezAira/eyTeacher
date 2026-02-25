package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.UpdatePasswordRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface UpdatePasswordUseCase {
    Result<Void, Integer> updatePassword(UpdatePasswordRequest request);
}
