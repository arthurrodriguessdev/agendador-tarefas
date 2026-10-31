package agendador.agendadortarefas.business.dto.tarefa;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TarefaAtualizarDTO(
        @Size(min = 10, max = 100,
                message = "O nome deve ter entre 10 e 100 caracteres")
        String nomeTarefa,

        @Size(min = 10, max = 500,
                message = "A descrição deve ter entre 10 e 500 caracteres")
        String descricaoTarefa,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        @Future(message = "A data do evento deve ser no futuro")
        LocalDateTime dataEvento
){}