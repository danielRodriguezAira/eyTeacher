package es.leinadfonfria.eyteacher.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ infrastructure configuration.
 *
 * <p>Topology used: a single {@code DirectExchange} receives all notification events.
 * Each event type has its own durable queue bound to the exchange via a dedicated routing key.
 * Messages are serialized and deserialized as JSON using Jackson.</p>
 *
 * <pre>
 *  Publisher  ──►  eyteacher.notifications (DirectExchange)
 *                      │── notification.new-task        ──►  notifications.new-task (Queue)
 *                      │── notification.new-solution    ──►  notifications.new-solution (Queue)
 *                      │── notification.new-correction  ──►  notifications.new-correction (Queue)
 *                      └── notification.new-subscription──►  notifications.new-subscription (Queue)
 * </pre>
 */
@Configuration
public class RabbitMQConfig {

    /** Name of the direct exchange that routes all notification events. */
    public static final String EXCHANGE = "eyteacher.notifications";

    /** Queue and routing key constants for each notification type. */
    public static final String QUEUE_NEW_TASK         = "notifications.new-task";
    public static final String QUEUE_NEW_SOLUTION     = "notifications.new-solution";
    public static final String QUEUE_NEW_CORRECTION   = "notifications.new-correction";
    public static final String QUEUE_NEW_SUBSCRIPTION = "notifications.new-subscription";

    public static final String ROUTING_KEY_NEW_TASK         = "notification.new-task";
    public static final String ROUTING_KEY_NEW_SOLUTION     = "notification.new-solution";
    public static final String ROUTING_KEY_NEW_CORRECTION   = "notification.new-correction";
    public static final String ROUTING_KEY_NEW_SUBSCRIPTION = "notification.new-subscription";

    // -------------------------------------------------------------------------
    // Exchange
    // -------------------------------------------------------------------------

    /**
     * Declares the direct exchange that receives all notification messages.
     * Durable: survives a RabbitMQ broker restart.
     *
     * @return The configured {@link DirectExchange}.
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    // -------------------------------------------------------------------------
    // Queues (durable = true so messages survive broker restarts)
    // -------------------------------------------------------------------------

    /** Declares the queue for new task notifications. */
    @Bean
    public Queue newTaskQueue() {
        return new Queue(QUEUE_NEW_TASK, true);
    }

    /** Declares the queue for new solution notifications. */
    @Bean
    public Queue newSolutionQueue() {
        return new Queue(QUEUE_NEW_SOLUTION, true);
    }

    /** Declares the queue for new correction notifications. */
    @Bean
    public Queue newCorrectionQueue() {
        return new Queue(QUEUE_NEW_CORRECTION, true);
    }

    /** Declares the queue for new subscription notifications. */
    @Bean
    public Queue newSubscriptionQueue() {
        return new Queue(QUEUE_NEW_SUBSCRIPTION, true);
    }

    // -------------------------------------------------------------------------
    // Bindings (queue ↔ exchange ↔ routing key)
    // -------------------------------------------------------------------------

    /**
     * Binds the new-task queue to the exchange using its routing key.
     *
     * @param newTaskQueue         The queue bean.
     * @param notificationExchange The exchange bean.
     * @return The configured {@link Binding}.
     */
    @Bean
    public Binding newTaskBinding(Queue newTaskQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(newTaskQueue).to(notificationExchange).with(ROUTING_KEY_NEW_TASK);
    }

    /**
     * Binds the new-solution queue to the exchange using its routing key.
     *
     * @param newSolutionQueue     The queue bean.
     * @param notificationExchange The exchange bean.
     * @return The configured {@link Binding}.
     */
    @Bean
    public Binding newSolutionBinding(Queue newSolutionQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(newSolutionQueue).to(notificationExchange).with(ROUTING_KEY_NEW_SOLUTION);
    }

    /**
     * Binds the new-correction queue to the exchange using its routing key.
     *
     * @param newCorrectionQueue   The queue bean.
     * @param notificationExchange The exchange bean.
     * @return The configured {@link Binding}.
     */
    @Bean
    public Binding newCorrectionBinding(Queue newCorrectionQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(newCorrectionQueue).to(notificationExchange).with(ROUTING_KEY_NEW_CORRECTION);
    }

    /**
     * Binds the new-subscription queue to the exchange using its routing key.
     *
     * @param newSubscriptionQueue The queue bean.
     * @param notificationExchange The exchange bean.
     * @return The configured {@link Binding}.
     */
    @Bean
    public Binding newSubscriptionBinding(Queue newSubscriptionQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(newSubscriptionQueue).to(notificationExchange).with(ROUTING_KEY_NEW_SUBSCRIPTION);
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    /**
     * Configures Jackson as the JSON message converter for both publishing and consuming.
     *
     * @return The {@link MessageConverter} backed by Jackson.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Overrides the default {@link RabbitTemplate} to use the JSON message converter.
     * This ensures all outbound messages are serialized as JSON.
     *
     * @param connectionFactory The AMQP connection factory provided by Spring Boot auto-configuration.
     * @return The configured {@link RabbitTemplate}.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Overrides the default listener container factory to use the JSON message converter.
     * This ensures all inbound messages are deserialized from JSON before reaching {@code @RabbitListener} methods.
     *
     * @param connectionFactory The AMQP connection factory provided by Spring Boot auto-configuration.
     * @return The configured {@link SimpleRabbitListenerContainerFactory}.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
