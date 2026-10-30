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

    private Tarefa getTarefaById(Long id, String email){
        return tarefaRepository.findByIdAndEmailUsuario(id, email).orElseThrow(
                () -> new TarefaNotFound("Tarefa com id " + id + " não encontrada.")
        );
    }

    public TarefaDTO adicionarTarefa(TarefaDTO tarefaDTO, String token){
        String emailUsuario = usuarioService.getEmailUsuario(token);
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaDTO);
        tarefaAdicionar.setEmailUsuario(emailUsuario);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefaAdicionar));
    }

    public List<TarefaDTO> buscarTarefasPorEmail(String token){
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(tarefaRepository.findByEmailUsuario(email));
    }

    public List<TarefaDTO> buscarTarefasPorIntervaloDatas(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String token
    ){
        String email = usuarioService.getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(
                tarefaRepository.findByEmailUsuarioAndDataEventoBetween(email, dataInicio, dataFim));
    }

    public void deletarTarefa(Long id, String token) {
        tarefaRepository.delete(getTarefaById(id, usuarioService.getEmailUsuario(token)));
    }

    public TarefaDTO alterarStatus(StatusNotificacaoEnum status, Long id, String token){
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));
        tarefa.setStatus(status);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizarDadosTarefa(TarefaDTO tarefaDTO, Long id, String token){
        Tarefa tarefa = getTarefaById(id, usuarioService.getEmailUsuario(token));
        tarefaUpdateMapper.update(tarefaDTO, tarefa);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    private List<TarefaDTO> getTarefasNotificar(String token){
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

    public void notificarProximasTarefas(String token){
        log.info("Iniciando processo de notificação das próximas tarefas");
        List<TarefaDTO> tarefasNotificar = getTarefasNotificar(token);
        log.info("Quantidade de tarefas a notificar: {}", tarefasNotificar.size());

        tarefasNotificar.forEach(tarefa->{
            taskPublisher.publisher(TaskPublisher.NEXT_TASK_TOPIC, tarefa);
            // alterarStatus(StatusNotificacaoEnum.NOTIFICADO, tarefa.getId(), token);
            // log.info("Notificação da tarefa {} enviada com sucesso", tarefa.getId());
        });
    }
}