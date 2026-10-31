package agendador.agendadortarefas.business.dto.tarefa;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TarefaAdicionarDTO(
        @NotBlank(message = "O nome da tarefa é obrigatório")
        @Size(min = 10, max = 100,
                message = "O nome deve ter entre 10 e 100 caracteres")
        String nomeTarefa,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 10, max = 500,
                message = "A descrição deve ter entre 10 e 500 caracteres")
        String descricaoTarefa,

        @NotNull(message = "A data do evento é obrigatória")
        @Future(message = "A data do evento deve ser no futuro")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataEvento
){}