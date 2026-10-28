package agendador.agendadortarefas.infraestructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskPublisher extends TaskPublisherAbs{
    public static final String TASK_CREATED_TOPIC = "created-task";
    public static final String NEXT_TASK_TOPIC = "next-task";
}
