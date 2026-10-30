package agendador.agendadortarefas.exception;

public class TarefaNotFound extends RuntimeException {
    public TarefaNotFound(String message) {
        super(message);
    }
    public TarefaNotFound(String message, Throwable cause){ super(message, cause); }
}
