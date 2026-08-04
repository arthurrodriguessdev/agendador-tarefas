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

    private Tarefa getTarefaById(Long id){
        return tarefaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("A tarefa não existe.")
        );
    }

    public TarefaDTO adicionarTarefa(TarefaDTO tarefaDTO, String token){
        tarefaDTO.setDataCriacao(LocalDateTime.now());
        tarefaDTO.setStatus(StatusNotificacaoEnum.PENDENTE);

        // Setando o e-mail do usuário autenticado
        tarefaDTO.setEmailUsuario(jwtUtil.extractUsername(getTokenFormatado(token)));
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaDTO);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefaAdicionar));
    }

    public List<TarefaDTO> buscarTarefasPorEmail(String token){
        String email = jwtUtil.extractUsername(getTokenFormatado(token));
        return tarefaMapper.toTarefasDTO(tarefaRepository.findByEmailUsuario(email));
    }

    public List<TarefaDTO> buscarTarefasPorIntervaloDatas(LocalDateTime dataInicio, LocalDateTime dataFim){
        return tarefaMapper.toTarefasDTO(tarefaRepository.buscarTarefasPorIntervaloDatas(dataInicio, dataFim));
    }

    public void deletarTarefa(Long id) {
        tarefaRepository.delete(getTarefaById(id));
    }

    public TarefaDTO alterarStatus(StatusNotificacaoEnum status, Long id){
        Tarefa tarefa = getTarefaById(id);
        tarefa.setStatus(status);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizarDadosTarefa(TarefaDTO tarefaDTO, Long id){
        Tarefa tarefa = getTarefaById(id);
        tarefaUpdateMapper.update(tarefaDTO, tarefa);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefa));
    }
}