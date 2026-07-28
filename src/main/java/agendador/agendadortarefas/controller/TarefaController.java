package agendador.agendadortarefas.controller;

import agendador.agendadortarefas.business.TarefaService;
import agendador.agendadortarefas.business.dto.TarefaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<TarefaDTO> adicionarTarefa(@RequestBody TarefaDTO tarefaDTO,
                                                     @RequestHeader("Authorization") String token){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarefaService.adicionarTarefa(tarefaDTO, token));
    }
}
