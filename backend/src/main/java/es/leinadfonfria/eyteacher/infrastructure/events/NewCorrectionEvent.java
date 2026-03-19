package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.domain.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewCorrectionEvent extends ApplicationEvent {
    private final User teacher;
    private final Long solutionId;

    public NewCorrectionEvent(Object source, User teacher, Long solutionId) {
        super(source);
        this.teacher = teacher;
        this.solutionId = solutionId;
    }
}
