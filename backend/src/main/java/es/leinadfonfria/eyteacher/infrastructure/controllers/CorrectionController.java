package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.correction.AddCorrectionRequest;
import es.leinadfonfria.eyteacher.application.services.correction.AddCorrectionUseCase;
import es.leinadfonfria.eyteacher.application.services.correction.GetCorrectionBySolutionIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for correction operations.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/corrections")
@RequiredArgsConstructor
@Tag(name = "Corrections", description = "Endpoints for correction management")
public class CorrectionController {
    private final AddCorrectionUseCase addCorrectionUseCase;
    private final GetCorrectionBySolutionIdUseCase getCorrectionBySolutionIdUseCase;

    /**
     * Handles correction addition requests.
     *
     * @param request The correction details.
     * @return ResponseEntity<?> HTTP 200 with the correction ID or BAD_REQUEST with an error code.
     */
    @PutMapping
    @Operation(summary = "Add correction", description = "Adds a correction for a specific solution. Only for TEACHER role.")
    public ResponseEntity<?> addCorrection(@RequestBody AddCorrectionRequest request) {
        log.info("Adding correction: {}", request);
        return addCorrectionUseCase.addCorrection(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves a correction by solution ID.
     *
     * @param solutionId The ID of the solution.
     * @return ResponseEntity<?> HTTP 200 with the correction or BAD_REQUEST with an error code.
     */
    @GetMapping("/solution/{solutionId}")
    @Operation(summary = "Get correction by solution", description = "Retrieves the correction for the given solution.")
    public ResponseEntity<?> getCorrectionBySolutionId(@PathVariable Long solutionId) {
        log.info("Getting correction for solution: {}", solutionId);
        return getCorrectionBySolutionIdUseCase.getCorrectionBySolutionId(solutionId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
