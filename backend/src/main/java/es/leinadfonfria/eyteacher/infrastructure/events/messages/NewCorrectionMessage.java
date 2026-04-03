package es.leinadfonfria.eyteacher.infrastructure.events.messages;

/**
 * Message published to RabbitMQ when a teacher submits a correction for a solution.
 *
 * @param correctionId    The ID of the created correction.
 * @param teacherFullName The full name of the teacher who wrote the correction.
 * @param solutionId      The ID of the corrected solution, used by the consumer to look up
 *                        the student and task details needed for the notification.
 */
public record NewCorrectionMessage(
        Long correctionId,
        String teacherFullName,
        Long solutionId
) {}
