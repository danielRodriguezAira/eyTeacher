package es.leinadfonfria.eyteacher.ai.controller;


import es.leinadfonfria.eyteacher.ai.dto.HintRequest;
import es.leinadfonfria.eyteacher.ai.dto.HintResponse;
import es.leinadfonfria.eyteacher.ai.service.HintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hints")
@Tag(name = "Hints", description = "Endpoints para la generación de pistas con IA")
public class HintController {

    private final HintService hintService;

    public HintController(HintService hintService) {
        this.hintService = hintService;
    }

    @PostMapping
    @Operation(summary = "Genera una pista", description = "Recibe el enunciado de un ejercicio y devuelve una pista conceptual sin dar la solución.")
    public ResponseEntity<HintResponse> getHint(@RequestBody HintRequest request) {
        if (request.exercise() == null || request.exercise().isBlank()) {
            return ResponseEntity.badRequest().body(new HintResponse("El enunciado del ejercicio no puede estar vacío."));
        }

        String hint = hintService.generateHint(request.exercise());
        return ResponseEntity.ok(new HintResponse(hint));
    }
}
