package agendador.agendadortarefas.business.dto;

import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TarefaDTO {
    private Long id;
    private String nomeTarefa;
    private String descricaoTarefa;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss") // Permite enviar como string e converte
    private LocalDateTime dataEvento;
    private String emailUsuario;
    private StatusNotificacaoEnum status;
}