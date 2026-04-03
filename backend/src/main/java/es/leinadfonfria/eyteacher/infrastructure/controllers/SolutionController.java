package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionRequest;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionUseCase;
import es.leinadfonfria.eyteacher.application.services.solution.GetSolutionByIdUseCase;
import es.leinadfonfria.eyteacher.application.services.solution.GetSolutionsByTaskUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for solution operations.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/solutions")
@RequiredArgsConstructor
@Tag(name = "Solutions", description = "Endpoints for solution management")
public class SolutionController {

    private final AddSolutionUseCase addSolutionUseCase;
    private final GetSolutionsByTaskUseCase getSolutionsByTaskUseCase;
    private final GetSolutionByIdUseCase getSolutionByIdUseCase;

    /**
     * Handles solution addition requests.
     *
     * @param request The solution details.
     * @return ResponseEntity<?> HTTP 200 with the solution ID or BAD_REQUEST with an error code.
     */
    @PutMapping
    @Operation(summary = "Add solution", description = "Adds a solution for a specific task. Only for STUDENT role.")
    public ResponseEntity<?> addSolution(@RequestBody AddSolutionRequest request) {
        log.info("Adding solution: {}", request);
        return addSolutionUseCase.addSolution(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves a solution by its ID.
     *
     * @param id The ID of the solution.
     * @return ResponseEntity<?> HTTP 200 with the solution or BAD_REQUEST with an error code.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get solution by ID", description = "Retrieves a solution by its ID.")
    public ResponseEntity<?> getSolutionById(@PathVariable Long id) {
        log.info("Getting solution by id: {}", id);
        return getSolutionByIdUseCase.getSolutionById(id)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves a page of solutions by task ID.
     * Teachers see all student solutions; students see only their own.
     *
     * @param taskId The ID of the task.
     * @param page   Zero-based page number (default 0).
     * @param size   Number of items per page (default 10).
     * @return ResponseEntity<?> HTTP 200 with the page of solutions or BAD_REQUEST with an error code.
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get solutions by task", description = "Retrieves a page of solutions for the given task.")
    public ResponseEntity<?> getSolutionsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting solutions for task: {}, page: {}, size: {}", taskId, page, size);
        return getSolutionsByTaskUseCase.getSolutionsByTask(taskId, page, size)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
