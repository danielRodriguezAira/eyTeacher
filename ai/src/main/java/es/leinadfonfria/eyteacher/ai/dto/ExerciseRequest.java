package es.leinadfonfria.eyteacher.ai.dto;

/**
 * Request DTO for exercise generation.
 *
 * @param task description of the topic and type of exercise to generate,
 *             e.g. "Física - Mecánica - Genérame un ejercicio sobre las leyes de Newton"
 */
public record ExerciseRequest(String task) {
}
