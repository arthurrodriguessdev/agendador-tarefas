package agendador.agendadortarefas.controller;

import agendador.agendadortarefas.business.TarefaService;
import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import agendador.agendadortarefas.infraestructure.messaging.TaskPublisher;
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
    private final TaskPublisher taskPublisher;

    @PostMapping
    public ResponseEntity<TarefaDTO> adicionarTarefa(@RequestBody TarefaDTO tarefaDTO,
                                                     @RequestHeader("Authorization") String token){
        TarefaDTO response = tarefaService.adicionarTarefa(tarefaDTO, token);
        taskPublisher.publisher(TaskPublisher.TASK_CREATED_TOPIC, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestHeader("Authorization") String token){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tarefaService.buscarTarefasPorIntervaloDatas(dataInicio, dataFim, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token){
        tarefaService.deletarTarefa(id, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarDadosTarefa(@RequestBody TarefaDTO tarefaDTO,
                                                          @PathVariable Long id,
                                                          @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.OK)
                .body(tarefaService.atualizarDadosTarefa(tarefaDTO, id, token));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarStatusTarefa(@RequestParam("status") StatusNotificacaoEnum status,
                                                           @PathVariable Long id,
                                                           @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.OK)
                .body(tarefaService.alterarStatus(status, id, token));
    }

    @PostMapping("/proximas/notificar")
    public ResponseEntity<Void> notificarProximasTarefas(@RequestHeader("Authorization") String token){
        tarefaService.notificarProximasTarefas(token);
        return ResponseEntity.ok().build();
    }
}