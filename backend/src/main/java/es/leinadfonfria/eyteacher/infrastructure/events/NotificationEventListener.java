package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationRequest;
import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationUseCase;
import es.leinadfonfria.eyteacher.domain.entities.NotificationEntityType;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.SolutionRepository;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import es.leinadfonfria.eyteacher.infrastructure.config.RabbitMQConfig;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewCorrectionMessage;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewSolutionMessage;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewSubscriptionMessage;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewTaskMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes notification messages from RabbitMQ queues and creates the corresponding
 * in-app notifications for teachers and students.
 *
 * <p>Each method listens to a dedicated queue declared in {@link RabbitMQConfig}.
 * Messages arrive as JSON and are deserialized automatically by the configured
 * Jackson converter.</p>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final AddNotificationUseCase addNotificationUseCase;
    private final SolutionRepository<Solution> solutionRepository;
    private final TaskRepository<Task> taskRepository;

    /**
     * Notifies each enrolled student that a new or updated task is available.
     *
     * @param message The deserialized message from the {@code notifications.new-task} queue.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NEW_TASK)
    public void onNewTask(NewTaskMessage message) {
        try {
            String text = message.teacherFullName() + " ha creado/modificado la tarea: "
                    + message.topicName() + " - " + message.taskDescription();
            for (java.util.UUID studentId : message.studentIds()) {
                addNotificationUseCase.addNotification(
                        new AddNotificationRequest(studentId, text, NotificationEntityType.TASK, message.taskId()));
            }
        } catch (Exception e) {
            log.error("Error creating notification for new-task message: {}", message, e);
        }
    }

    /**
     * Notifies the teacher that a student has submitted a solution.
     *
     * @param message The deserialized message from the {@code notifications.new-solution} queue.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NEW_SOLUTION)
    public void onNewSolution(NewSolutionMessage message) {
        try {
            String text = message.studentFullName() + " ha entregado una solución a la tarea "
                    + message.topicName() + " - " + message.taskDescription();
            addNotificationUseCase.addNotification(
                    new AddNotificationRequest(message.teacherId(), text, NotificationEntityType.SOLUTION, message.solutionId()));
        } catch (Exception e) {
            log.error("Error creating notification for new-solution message: {}", message, e);
        }
    }

    /**
     * Notifies the student that their solution has been corrected.
     *
     * @param message The deserialized message from the {@code notifications.new-correction} queue.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NEW_CORRECTION)
    public void onNewCorrection(NewCorrectionMessage message) {
        try {
            Solution solution = solutionRepository.findById(message.solutionId())
                    .orElseThrow(() -> new NotFoundException("Solution not found", ErrorCode.SOLUTION_NOT_FOUND));

            Task task = taskRepository.findById(solution.getTask().getId());
            String text = message.teacherFullName() + " ha realizado una corrección en la tarea: "
                    + task.getTopic().getName().value() + " - " + task.getDescription();

            addNotificationUseCase.addNotification(
                    new AddNotificationRequest(
                            solution.getStudent().getId().value(),
                            text,
                            NotificationEntityType.CORRECTION,
                            message.solutionId()));
        } catch (Exception e) {
            log.error("Error creating notification for new-correction message: {}", message, e);
        }
    }

    /**
     * Notifies each newly subscribed student that they have been added to a topic.
     *
     * @param message The deserialized message from the {@code notifications.new-subscription} queue.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NEW_SUBSCRIPTION)
    public void onNewSubscription(NewSubscriptionMessage message) {
        try {
            String text = message.teacherFullName() + " te ha suscrito al tema: " + message.topicName();
            for (java.util.UUID studentId : message.studentIds()) {
                addNotificationUseCase.addNotification(
                        new AddNotificationRequest(studentId, text, NotificationEntityType.TOPIC, message.topicId()));
            }
        } catch (Exception e) {
            log.error("Error creating notification for new-subscription message: {}", message, e);
        }
    }
}
