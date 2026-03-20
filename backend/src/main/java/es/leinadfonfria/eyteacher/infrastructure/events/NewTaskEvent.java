package es.leinadfonfria.eyteacher.infrastructure.events;

import es.leinadfonfria.eyteacher.domain.entities.Task;
import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class NewTaskEvent extends ApplicationEvent {
    private final Task task;
    private final List<User> studentList;

    public NewTaskEvent(Object source, Task task, List<User> studentList) {
        super(source);
        this.task = task;
        this.studentList = studentList;
    }
}
