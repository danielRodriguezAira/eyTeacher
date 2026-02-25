package es.leinadfonfria.eyteacher.application.services.auth;

import es.leinadfonfria.eyteacher.application.dtos.auth.RegisterRequest;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface RegisterUseCase {
    Result<Void, Integer> register(RegisterRequest request);
}
