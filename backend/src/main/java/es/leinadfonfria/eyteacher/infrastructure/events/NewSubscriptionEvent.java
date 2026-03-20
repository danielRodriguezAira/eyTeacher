package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class NewSubscriptionEvent extends ApplicationEvent {
    private final Topic topic;
    private final List<User> newStudents;

    public NewSubscriptionEvent(Object source, Topic topic, List<User> newStudents) {
        super(source);
        this.topic = topic;
        this.newStudents = newStudents;
    }
}
