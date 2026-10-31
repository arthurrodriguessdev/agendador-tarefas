package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.dto.tarefa.TarefaAdicionarDTO;
import agendador.agendadortarefas.business.dto.tarefa.TarefaAtualizarDTO;
import agendador.agendadortarefas.business.dto.tarefa.TarefaResponseDTO;
import agendador.agendadortarefas.business.mapper.TarefaMapper;
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
     * @param tarefaAdicionarDTO dados da tarefa enviados pelo cliente
     * @param token token bearer
     * @return tarefa persistida
     */
    public TarefaResponseDTO adicionarTarefa(TarefaAdicionarDTO tarefaAdicionarDTO, String token) {
        String emailUsuario = usuarioService.getEmailUsuario(token);
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaAdicionarDTO);

        // Setando valores que não são passados por parâmetros
        tarefaAdicionar.setEmailUsuario(emailUsuario);
        tarefaAdicionar.setDataCriacao(LocalDateTime.now());
        tarefaAdicionar.setStatus(StatusNotificacaoEnum.PENDENTE);

        TarefaResponseDTO tarefaCriada = tarefaMapper.toTarefaResponseDTO(tarefaRepository.save(tarefaAdicionar));
        taskPublisher.publisher(TaskPublisher.TASK_CREATED_TOPIC, tarefaCriada);
        return tarefaCriada;
    }

    /**
     * Retorna todas as tarefas do usuário identificado pelo token.
     *
     * @param token token bearer
     * @return tarefas do usuário ou uma lista vazia quando não houver tarefas
     */
    public List<TarefaResponseDTO> buscarTarefasPorEmail(String token) {
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasResponseDTO(tarefaRepository.findByEmailUsuario(email));
    }

    /**
     * Retorna as tarefas do usuário autenticado dentro do período informado.
     *
     * @param dataInicio início inclusivo do período
     * @param dataFim fim inclusivo do período
     * @param token token bearer
     * @return tarefas encontradas no período ou uma lista vazia
     */
    public List<TarefaResponseDTO> buscarTarefasPorIntervaloDatas(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String token
    ) {
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasResponseDTO(
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
    public TarefaResponseDTO alterarStatus(StatusNotificacaoEnum status, Long id, String token) {
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));
        tarefa.setStatus(status);
        return tarefaMapper.toTarefaResponseDTO(tarefaRepository.save(tarefa));
    }

    /**
     * Atualiza os campos informados de uma tarefa pertencente ao usuário autenticado.
     *
     * @param tarefaAtualizarDTO valores para atualização
     * @param id identificador da tarefa
     * @param token token bearer
     * @return tarefa atualizada
     * @throws TarefaNotFound quando a tarefa não existe ou não pertence ao usuário
     */
    public TarefaResponseDTO atualizarDadosTarefa(TarefaAtualizarDTO tarefaAtualizarDTO, Long id, String token) {
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));

        // Atualizando os dados da tarefa
        if(tarefaAtualizarDTO.nomeTarefa() != null && !tarefaAtualizarDTO.nomeTarefa().isBlank()){
            tarefa.setNomeTarefa(tarefaAtualizarDTO.nomeTarefa());
        }

        if(tarefaAtualizarDTO.descricaoTarefa() != null && !tarefaAtualizarDTO.descricaoTarefa().isBlank()){
            tarefa.setDescricaoTarefa(tarefaAtualizarDTO.descricaoTarefa());
        }

        if(tarefaAtualizarDTO.dataEvento() != null){
            tarefa.setDataEvento(tarefaAtualizarDTO.dataEvento());
        }

        return tarefaMapper.toTarefaResponseDTO(tarefaRepository.save(tarefa));
    }

    private List<TarefaResponseDTO> getTarefasNotificar(String token) {
        LocalDateTime horaAtual = LocalDateTime.now();
        LocalDateTime horaAtualMaisUma = horaAtual.plusHours(1);
        List<TarefaResponseDTO> tarefasPeriodo = buscarTarefasPorIntervaloDatas(
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
        List<TarefaResponseDTO> tarefasNotificar = getTarefasNotificar(token);
        log.info("Quantidade de tarefas a notificar: {}", tarefasNotificar.size());

        tarefasNotificar.forEach(tarefa -> {
            taskPublisher.publisher(TaskPublisher.NEXT_TASK_TOPIC, tarefa);
        });
    }
}