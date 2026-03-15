package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionRequest;
import es.leinadfonfria.eyteacher.application.services.solution.AddSolutionUseCase;
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
     * Retrieves solutions by task ID.
     *
     * @param taskId The ID of the task.
     * @return ResponseEntity<?> HTTP 200 with the list of solutions or BAD_REQUEST with an error code.
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get solutions by task", description = "Retrieves all solutions for the given task. Only for TEACHER role.")
    public ResponseEntity<?> getSolutionsByTask(@PathVariable Long taskId) {
        log.info("Getting solutions for task: {}", taskId);
        return getSolutionsByTaskUseCase.getSolutionsByTask(taskId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
