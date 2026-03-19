package es.leinadfonfria.eyteacher.application.services.correction;

import es.leinadfonfria.eyteacher.application.dtos.correction.CorrectionResponse;
import es.leinadfonfria.eyteacher.application.shared.Result;

public interface GetCorrectionBySolutionIdUseCase {
    Result<CorrectionResponse, Integer> getCorrectionBySolutionId(Long solutionId);
}
