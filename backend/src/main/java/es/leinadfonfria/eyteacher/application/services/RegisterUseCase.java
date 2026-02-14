package es.leinadfonfria.eyteacher.application.services;

import es.leinadfonfria.eyteacher.application.dtos.RegisterRequest;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;

public interface RegisterUseCase {
    UserId register(RegisterRequest request);
}
