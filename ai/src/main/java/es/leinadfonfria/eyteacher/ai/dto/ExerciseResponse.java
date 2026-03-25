package es.leinadfonfria.eyteacher.ai.dto;

/**
 * Response DTO containing the AI-generated exercise proposal.
 *
 * @param taskProposal the generated exercise statement
 */
public record ExerciseResponse(String taskProposal) {
}
