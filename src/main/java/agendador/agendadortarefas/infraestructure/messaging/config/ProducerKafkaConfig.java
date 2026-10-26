package agendador.agendadortarefas.infraestructure.messaging.config;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerKafkaConfig {
    @Bean
    public ProducerFactory<String, TarefaDTO> producerFactory(){
        Map<String, Object> mapConfigs = new HashMap<>();
        mapConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        mapConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        mapConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(mapConfigs);
    }

    @Bean
    public KafkaTemplate<String, TarefaDTO> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic tarefaCriadaTopic(){
        return TopicBuilder.name("tarefa-criada")
                .partitions(1)
                .replicas(1)
                .build();
    }
}