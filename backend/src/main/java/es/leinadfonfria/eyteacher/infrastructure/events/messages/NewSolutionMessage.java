package es.leinadfonfria.eyteacher.infrastructure.events.messages;

import java.util.UUID;

/**
 * Message published to RabbitMQ when a student submits a solution to a task.
 * Contains all data needed by the notification consumer without requiring further DB lookups.
 *
 * @param solutionId      The ID of the submitted solution.
 * @param taskId          The ID of the task the solution belongs to.
 * @param taskDescription The task description shown in the notification.
 * @param topicName       The name of the topic the task belongs to.
 * @param studentFullName The full name of the student who submitted the solution.
 * @param teacherId       The UUID of the teacher to notify.
 */
public record NewSolutionMessage(
        Long solutionId,
        Long taskId,
        String taskDescription,
        String topicName,
        String studentFullName,
        UUID teacherId
) {}
