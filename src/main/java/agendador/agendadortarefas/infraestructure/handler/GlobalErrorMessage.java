package agendador.agendadortarefas.infraestructure.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class GlobalErrorMessage {
    private HttpStatus status;
    private String message;
}