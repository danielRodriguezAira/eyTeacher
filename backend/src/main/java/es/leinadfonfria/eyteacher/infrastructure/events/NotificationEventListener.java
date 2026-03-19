package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationRequest;
import es.leinadfonfria.eyteacher.application.services.notification.AddNotificationUseCase;
import es.leinadfonfria.eyteacher.domain.entities.Solution;
import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.SolutionRepository;
import es.leinadfonfria.eyteacher.domain.ports.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final AddNotificationUseCase addNotificationUseCase;
    private final SolutionRepository<Solution> solutionRepository;
    private final TaskRepository<Task> taskRepository;

    @EventListener
    public void onNewSolution(NewSolutionEvent event) {
        try {
            Solution solution = event.getSolution();
            User student = solution.getStudent();
            Task task = solution.getTask();
            User teacher = task.getTopic().getCategory().getOwner();

            String studentName = student.getFirstName().value() + " " + student.getLastName().value();
            String topicName = task.getTopic().getName().value();
            String message = "El alumno " + studentName + " ha entregado una solución a la tarea " + topicName + " - " + task.getDescription();
            String goTo = "/tasks/" + task.getId();

            addNotificationUseCase.addNotification(new AddNotificationRequest(teacher.getId().value(), message, goTo));
        } catch (Exception e) {
            log.error("Error creating notification for new solution event", e);
        }
    }

    @EventListener
    public void onNewCorrection(NewCorrectionEvent event) {
        try {
            User teacher = event.getTeacher();
            Long solutionId = event.getSolutionId();

            Solution solution = solutionRepository.findById(solutionId)
                    .orElseThrow(() -> new NotFoundException("Solution not found", ErrorCode.SOLUTION_NOT_FOUND));
            User student = solution.getStudent();

            Task task = taskRepository.findById(solution.getTask().getId());
            String topicName = task.getTopic().getName().value();

            String teacherName = teacher.getFirstName().value() + " " + teacher.getLastName().value();
            String message = teacherName + " ha realizado una corrección en la tarea: " + topicName + " - " + task.getDescription();
            String goTo = "/tasks/" + task.getId();

            addNotificationUseCase.addNotification(new AddNotificationRequest(student.getId().value(), message, goTo));
        } catch (Exception e) {
            log.error("Error creating notification for new correction event", e);
        }
    }
}
