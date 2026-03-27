package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.infrastructure.config.RabbitMQConfig;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes notification messages to RabbitMQ.
 *
 * <p>Provides one method per notification type, each sending a serialized JSON message
 * to the {@code eyteacher.notifications} direct exchange with the appropriate routing key.</p>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishes a message to notify students that a new task has been created or updated.
     *
     * @param message The message containing task and student details.
     */
    public void publishNewTask(NewTaskMessage message) {
        log.debug("Publishing new-task message for task id={}", message.taskId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_NEW_TASK, message);
    }

    /**
     * Publishes a message to notify the teacher that a student has submitted a solution.
     *
     * @param message The message containing solution and teacher details.
     */
    public void publishNewSolution(NewSolutionMessage message) {
        log.debug("Publishing new-solution message for task id={}", message.taskId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_NEW_SOLUTION, message);
    }

    /**
     * Publishes a message to notify the student that a correction has been submitted.
     *
     * @param message The message containing teacher name and solution ID.
     */
    public void publishNewCorrection(NewCorrectionMessage message) {
        log.debug("Publishing new-correction message for solution id={}", message.solutionId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_NEW_CORRECTION, message);
    }

    /**
     * Publishes a message to notify students that they have been subscribed to a topic.
     *
     * @param message The message containing topic and student details.
     */
    public void publishNewSubscription(NewSubscriptionMessage message) {
        log.debug("Publishing new-subscription message for topic id={}", message.topicId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_NEW_SUBSCRIPTION, message);
    }
}
