package agendador.agendadortarefas.infraestructure.handler;

import agendador.agendadortarefas.exception.TarefaNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TarefaNotFound.class)
    private ResponseEntity<GlobalErrorMessage> tarefaNotFoundHandler(TarefaNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new GlobalErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
}