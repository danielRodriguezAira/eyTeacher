package es.leinadfonfria.eyteacher.application.services.correction;

import es.leinadfonfria.eyteacher.application.shared.Result;

public interface AddCorrectionUseCase {
    Result<Long, Integer> addCorrection(AddCorrectionRequest request);
}
