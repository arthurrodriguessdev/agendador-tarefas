package agendador.agendadortarefas.infraestructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskPublisher extends TaskPublisherAbs{
    public static final String TASK_CREATED_TOPIC = "created-task";
    public static final String NEXT_TASK_TOPIC = "next-task";

    public TaskPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }
}