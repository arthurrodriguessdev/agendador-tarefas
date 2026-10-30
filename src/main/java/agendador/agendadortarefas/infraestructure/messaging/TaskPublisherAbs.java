package agendador.agendadortarefas.infraestructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletionException;

@Slf4j
public abstract class TaskPublisherAbs {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public TaskPublisherAbs(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publisher(String topic, Object object){
        kafkaTemplate.send(topic, object)
                .thenAccept(result -> {
                    log.info("Evento do tópico '{}' publicado", topic);
                })
                .exceptionally(ex ->{
                    log.error("Erro ao publicar o evento do tópico {}", topic);
                    throw new CompletionException(ex);
                });
    }
}