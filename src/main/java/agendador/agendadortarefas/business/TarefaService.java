package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.business.mapper.TarefaMapper;
import agendador.agendadortarefas.business.mapper.TarefaUpdateMapper;
import agendador.agendadortarefas.exception.TarefaNotFound;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import agendador.agendadortarefas.infraestructure.messaging.TaskPublisher;
import agendador.agendadortarefas.infraestructure.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final TarefaMapper tarefaMapper;
    private final TarefaUpdateMapper tarefaUpdateMapper;
    private final TaskPublisher taskPublisher;
    private final UsuarioService usuarioService;

    private Tarefa getTarefaById(Long id, String email) {
        return tarefaRepository.findByIdAndEmailUsuario(id, email).orElseThrow(
                () -> new TarefaNotFound("Tarefa com ID " + id + " não encontrada.")
        );
    }

    /**
     * Persiste uma tarefa para o usuário identificado pelo token e solicita a publicação do evento de criação.
     *
     * @param tarefaDTO dados da tarefa enviados pelo cliente
     * @param token token bearer
     * @return tarefa persistida
     */
    public TarefaDTO adicionarTarefa(TarefaDTO tarefaDTO, String token) {
        String emailUsuario = usuarioService.getEmailUsuario(token);
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaDTO);
        tarefaAdicionar.setEmailUsuario(emailUsuario);
        TarefaDTO tarefaCriada = tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefaAdicionar));
        taskPublisher.publisher(TaskPublisher.TASK_CREATED_TOPIC, tarefaCriada);
        return tarefaCriada;
    }

    /**
     * Retorna todas as tarefas do usuário identificado pelo token.
     *
     * @param token token bearer
     * @return tarefas do usuário ou uma lista vazia quando não houver tarefas
     */
    public List<TarefaDTO> buscarTarefasPorEmail(String token) {
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(tarefaRepository.findByEmailUsuario(email));
    }

    /**
     * Retorna as tarefas do usuário autenticado dentro do período informado.
     *
     * @param dataInicio início inclusivo do período
     * @param dataFim fim inclusivo do período
     * @param token token bearer
     * @return tarefas encontradas no período ou uma lista vazia
     */
    public List<TarefaDTO> buscarTarefasPorIntervaloDatas(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String token
    ) {
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(
                tarefaRepository.findByEmailUsuarioAndDataEventoBetween(email, dataInicio, dataFim)
        );
    }

    /**
     * Exclui uma tarefa pertencente ao usuário identificado pelo token.
     *
     * @param id identificador da tarefa
     * @param token token bearer
     * @throws TarefaNotFound quando a tarefa não existe ou não pertence ao usuário
     */
    public void deletarTarefa(Long id, String token) {
        tarefaRepository.delete(getTarefaById(id, usuarioService.getEmailUsuario(token)));
    }

    /**
     * Altera o status de uma tarefa pertencente ao usuário autenticado.
     *
     * @param status novo status da notificação
     * @param id identificador da tarefa
     * @param token token bearer
     * @return tarefa atualizada
     * @throws TarefaNotFound quando a tarefa não existe ou não pertence ao usuário
     */
    public TarefaDTO alterarStatus(StatusNotificacaoEnum status, Long id, String token) {
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));
        tarefa.setStatus(status);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    /**
     * Atualiza os campos informados de uma tarefa pertencente ao usuário autenticado.
     *
     * @param tarefaDTO valores para atualização
     * @param id identificador da tarefa
     * @param token token bearer
     * @return tarefa atualizada
     * @throws TarefaNotFound quando a tarefa não existe ou não pertence ao usuário
     */
    public TarefaDTO atualizarDadosTarefa(TarefaDTO tarefaDTO, Long id, String token) {
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));
        tarefaUpdateMapper.update(tarefaDTO, tarefa);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    private List<TarefaDTO> getTarefasNotificar(String token) {
        LocalDateTime horaAtual = LocalDateTime.now();
        LocalDateTime horaAtualMaisUma = horaAtual.plusHours(1);
        List<TarefaDTO> tarefasPeriodo = buscarTarefasPorIntervaloDatas(
                horaAtual,
                horaAtualMaisUma,
                token
        );

        return tarefasPeriodo.stream()
                .filter(tarefa -> tarefa.status() == StatusNotificacaoEnum.PENDENTE)
                .toList();
    }

    /**
     * Publica os eventos das tarefas pendentes previstas para a próxima hora
     * para consumo pelo serviço notificador.
     *
     * @param token token bearer
     */
    public void notificarProximasTarefas(String token) {
        log.info("Iniciando processo de notificação das próximas tarefas");
        List<TarefaDTO> tarefasNotificar = getTarefasNotificar(token);
        log.info("Quantidade de tarefas a notificar: {}", tarefasNotificar.size());

        tarefasNotificar.forEach(tarefa -> {
            taskPublisher.publisher(TaskPublisher.NEXT_TASK_TOPIC, tarefa);
        });
    }
}