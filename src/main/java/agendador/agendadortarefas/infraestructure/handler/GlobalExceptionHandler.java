package agendador.agendadortarefas.infraestructure.handler;

import agendador.agendadortarefas.exception.TarefaNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TarefaNotFound.class)
    public ResponseEntity<GlobalErrorMessage> tarefaNotFoundHandler(TarefaNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new GlobalErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorMessage> methodArgumentNotValidHandler(MethodArgumentNotValidException ex){
        Map<String, String> mapErrors = new HashMap<>();
        List<FieldError> listErrors = ex.getBindingResult().getFieldErrors();

        // Mapeia o campo que deu erro e a mensagem padrão do DTO
        listErrors.forEach(error ->{
            mapErrors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        });

        // Concatenando as mensagens de erro em uma única String
        StringBuilder errorMessage = new StringBuilder();
        mapErrors.forEach((field, message)->{
            errorMessage.append(String.format("'%s': '%s'; ", field, message));
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new GlobalErrorMessage(HttpStatus.BAD_REQUEST, errorMessage.toString())
        );
    }
}