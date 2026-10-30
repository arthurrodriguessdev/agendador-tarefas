package agendador.agendadortarefas.controller;

import agendador.agendadortarefas.business.TarefaService;
import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
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

    /**
     * Cria uma tarefa para o usuário autenticado e solicita a publicação do evento de criação.
     *
     * @param tarefaDTO dados da tarefa enviados pelo cliente
     * @param token token bearer do cabeçalho Authorization
     * @return a tarefa criada
     */
    @PostMapping
    public ResponseEntity<TarefaDTO> adicionarTarefa(
            @RequestBody TarefaDTO tarefaDTO,
            @RequestHeader("Authorization") String token
    ) {
        TarefaDTO response = tarefaService.adicionarTarefa(tarefaDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista as tarefas do usuário autenticado.
     *
     * @param token token bearer do cabeçalho Authorization
     * @return tarefas do usuário
     */
    @GetMapping
    public ResponseEntity<List<TarefaDTO>> buscarTarefasPorEmail(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(tarefaService.buscarTarefasPorEmail(token));
    }

    /**
     * Lista as tarefas do usuário autenticado cuja data do evento está no período informado.
     *
     * @param dataInicio início inclusivo do período
     * @param dataFim fim inclusivo do período
     * @param token token bearer do cabeçalho Authorization
     * @return tarefas encontradas no período
     */
    @GetMapping("/eventos")
    public ResponseEntity<List<TarefaDTO>> buscarTarefasPorIntervaloDatas(
            @RequestParam("dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam("dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                tarefaService.buscarTarefasPorIntervaloDatas(dataInicio, dataFim, token)
        );
    }

    /**
     * Exclui uma tarefa pertencente ao usuário autenticado.
     *
     * @param id identificador da tarefa
     * @param token token bearer do cabeçalho Authorization
     * @return HTTP 204 após a exclusão
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        tarefaService.deletarTarefa(id, token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza os campos informados de uma tarefa pertencente ao usuário autenticado.
     *
     * @param tarefaDTO valores para atualização
     * @param id identificador da tarefa
     * @param token token bearer do cabeçalho Authorization
     * @return tarefa atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarDadosTarefa(
            @RequestBody TarefaDTO tarefaDTO,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(tarefaService.atualizarDadosTarefa(tarefaDTO, id, token));
    }

    /**
     * Altera o status de uma tarefa pertencente ao usuário autenticado.
     *
     * @param status novo status da notificação
     * @param id identificador da tarefa
     * @param token token bearer do cabeçalho Authorization
     * @return tarefa atualizada
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarStatusTarefa(
            @RequestParam("status") StatusNotificacaoEnum status,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(tarefaService.alterarStatus(status, id, token));
    }

    /**
     * Solicita a publicação das tarefas pendentes do usuário autenticado previstas para a próxima hora.
     *
     * @param token token bearer do cabeçalho Authorization
     * @return HTTP 200 após o envio das solicitações de publicação
     */
    @PostMapping("/proximas/notificar")
    public ResponseEntity<Void> notificarProximasTarefas(
            @RequestHeader("Authorization") String token
    ) {
        tarefaService.notificarProximasTarefas(token);
        return ResponseEntity.ok().build();
    }
}