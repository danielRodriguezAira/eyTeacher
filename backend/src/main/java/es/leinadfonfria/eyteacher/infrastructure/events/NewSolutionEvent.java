package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.domain.entities.Solution;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewSolutionEvent extends ApplicationEvent {
    private final Solution solution;

    public NewSolutionEvent(Object source, Solution solution) {
        super(source);
        this.solution = solution;
    }
}
