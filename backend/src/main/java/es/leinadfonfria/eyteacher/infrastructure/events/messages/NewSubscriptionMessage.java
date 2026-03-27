package es.leinadfonfria.eyteacher.infrastructure.events.messages;

import java.util.List;
import java.util.UUID;

/**
 * Message published to RabbitMQ when students are subscribed to a topic.
 * Contains all data needed by the notification consumer without requiring further DB lookups.
 *
 * @param topicId         The ID of the topic the students were subscribed to.
 * @param topicName       The name of the topic shown in the notification.
 * @param teacherFullName The full name of the teacher who owns the topic.
 * @param studentIds      The UUIDs of the newly subscribed students to notify.
 */
public record NewSubscriptionMessage(
        Long topicId,
        String topicName,
        String teacherFullName,
        List<UUID> studentIds
) {}
