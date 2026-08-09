package agendador.agendadortarefas.business.dto;

import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TarefaDTO(
        Long id,
        String nomeTarefa,
        String descricaoTarefa,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataCriacao,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataEvento,
        String emailUsuario,
        StatusNotificacaoEnum status
){}