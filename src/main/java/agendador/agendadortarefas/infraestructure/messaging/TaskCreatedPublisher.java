package agendador.agendadortarefas.infraestructure.messaging;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCreatedPublisher {
    public static final String TASK_CREATED_TOPIC = "tarefa-criada";
    private final KafkaTemplate<String, TarefaDTO> kafkaTemplate;

    public void publish(TarefaDTO tarefa){
        kafkaTemplate.send(TASK_CREATED_TOPIC, tarefa)
                .thenAccept(result ->{
                    log.info(
                            "Evento de tarefa publicada. topic={}, partition={}, offset={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                })
                .exceptionally(ex ->{
                    log.error("Falha ao publicar evento de tarefa");
                    throw new CompletionException(ex);
                });
    }
}
