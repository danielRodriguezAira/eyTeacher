package es.leinadfonfria.eyteacher.infrastructure.events.messages;

import java.util.List;
import java.util.UUID;

/**
 * Message published to RabbitMQ when a task is created or updated.
 * Contains all data needed by the notification consumer without requiring further DB lookups.
 *
 * @param taskId          The ID of the new or updated task.
 * @param taskDescription The task description shown in the notification.
 * @param topicName       The name of the topic the task belongs to.
 * @param teacherFullName The full name of the teacher who owns the topic.
 * @param studentIds      The UUIDs of the students enrolled in the topic.
 */
public record NewTaskMessage(
        Long taskId,
        String taskDescription,
        String topicName,
        String teacherFullName,
        List<UUID> studentIds
) {}
