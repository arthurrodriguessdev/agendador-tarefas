package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.business.mapper.TarefaMapper;
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
    private final JwtUtil jwtUtil;

    // Retira a inicial e espaçamento do token
    private String getTokenFormatado(String token){
        String prefixoToken = "Bearer ";
        if(token.startsWith(prefixoToken)){
            token = token.substring(prefixoToken.length());
        }

        return token;
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
        List<Tarefa> tarefas = tarefaRepository.findByEmailUsuario(email);
        if(tarefas.isEmpty()){
            throw new ResourceNotFound(
                    String.format("Nenhuma tarefa vinculada ao email: %s foi encontrada.", email));
        }

        return tarefaMapper.toTarefasDTO(tarefas);
    }

    public List<TarefaDTO> buscarTarefasPorIntervaloDatas(LocalDateTime dataInicio, LocalDateTime dataFim){
        List<Tarefa> tarefas = tarefaRepository.buscarTarefasPorIntervaloDatas(dataInicio, dataFim);
        if(tarefas.isEmpty()){
            throw new ResourceNotFound("Nenhuma tarefa foi encontrada nesse intervalo de datas.");
        }

        return tarefaMapper.toTarefasDTO(tarefas);
    }
}