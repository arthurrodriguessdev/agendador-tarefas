package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.business.mapper.TarefaMapper;
import agendador.agendadortarefas.business.mapper.TarefaUpdateMapper;
import agendador.agendadortarefas.exception.ResourceNotFound;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import agendador.agendadortarefas.infraestructure.repository.TarefaRepository;
import agendador.agendadortarefas.infraestructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final TarefaMapper tarefaMapper;
    private final TarefaUpdateMapper tarefaUpdateMapper;
    private final JwtUtil jwtUtil;

    // Retira a inicial e espaçamento do token
    private String getTokenFormatado(String token){
        String prefixoToken = "Bearer ";
        if(token.startsWith(prefixoToken)){
            token = token.substring(prefixoToken.length());
        }

        return token;
    }

    private String getEmailUsuario(String token) {
        return jwtUtil.extractUsername(getTokenFormatado(token));
    }

    private Tarefa getTarefaById(Long id, String email){
        return tarefaRepository.findByIdAndEmailUsuario(id, email).orElseThrow(
                () -> new ResourceNotFound("A tarefa não existe.")
        );
    }

    public TarefaDTO adicionarTarefa(TarefaDTO tarefaDTO, String token){
        String emailUsuario = getEmailUsuario(token);
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaDTO);
        tarefaAdicionar.setEmailUsuario(emailUsuario);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefaAdicionar));
    }

    public List<TarefaDTO> buscarTarefasPorEmail(String token){
        String email = getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(tarefaRepository.findByEmailUsuario(email));
    }

    public List<TarefaDTO> buscarTarefasPorIntervaloDatas(
            LocalDateTime dataInicio, LocalDateTime dataFim, String token) {
        String email = getEmailUsuario(token);
        return tarefaMapper.toTarefasDTO(
                tarefaRepository.findByEmailUsuarioAndDataEventoBetween(email, dataInicio, dataFim));
    }

    public void deletarTarefa(Long id, String token) {
        tarefaRepository.delete(getTarefaById(id, getEmailUsuario(token)));
    }

    public TarefaDTO alterarStatus(StatusNotificacaoEnum status, Long id, String token){
        Tarefa tarefa = getTarefaById(id, getEmailUsuario(token));
        tarefa.setStatus(status);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizarDadosTarefa(TarefaDTO tarefaDTO, Long id, String token){
        Tarefa tarefa = getTarefaById(id, getEmailUsuario(token));
        tarefaUpdateMapper.update(tarefaDTO, tarefa);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }
}