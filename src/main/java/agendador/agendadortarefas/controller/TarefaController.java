package agendador.agendadortarefas.controller;

import agendador.agendadortarefas.business.TarefaService;
import agendador.agendadortarefas.business.dto.TarefaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<TarefaDTO>> buscarTarefasPorEmail(@RequestHeader("Authorization") String token){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tarefaService.buscarTarefasPorEmail(token));
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<TarefaDTO>> buscarTarefasPorIntervaloDatas(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tarefaService.buscarTarefasPorIntervaloDatas(dataInicio, dataFim));
    }
}