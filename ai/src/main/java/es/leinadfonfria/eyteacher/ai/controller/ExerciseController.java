package es.leinadfonfria.eyteacher.ai.controller;

import es.leinadfonfria.eyteacher.ai.dto.ExerciseRequest;
import es.leinadfonfria.eyteacher.ai.dto.ExerciseResponse;
import es.leinadfonfria.eyteacher.ai.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AI-powered exercise generation.
 *
 * <p>Exposes endpoints that delegate exercise creation to the {@link ExerciseService},
 * which uses a language model to produce short, self-contained exercise statements.</p>
 */
@RestController
@RequestMapping("/api/v1/exercises")
@Tag(name = "Exercises", description = "Endpoints para la generación de ejercicios con IA")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * Creates an {@code ExerciseController} with the required service dependency.
     *
     * @param exerciseService the service responsible for generating exercise proposals
     */
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    /**
     * Generates a short exercise proposal based on the provided task description.
     *
     * @param request the request body containing the task description
     * @return {@code 200 OK} with the generated exercise proposal,
     *         or {@code 400 Bad Request} if the task is missing or blank
     */
    @PostMapping
    @Operation(
            summary = "Genera un ejercicio",
            description = "Recibe una descripción de tema e intención y devuelve un enunciado de ejercicio breve y autocontenido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicio generado correctamente"),
            @ApiResponse(responseCode = "400", description = "El campo 'task' está vacío o ausente")
    })
    public ResponseEntity<ExerciseResponse> generateExercise(@RequestBody ExerciseRequest request) {
        if (request.task() == null || request.task().isBlank()) {
            return ResponseEntity.badRequest().body(new ExerciseResponse("La descripción del ejercicio no puede estar vacía."));
        }

        String taskProposal = exerciseService.generateExercise(request.task());
        return ResponseEntity.ok(new ExerciseResponse(taskProposal));
    }
}
