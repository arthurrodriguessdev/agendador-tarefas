package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.business.mapper.TarefaMapper;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import agendador.agendadortarefas.infraestructure.repository.TarefaRepository;
import agendador.agendadortarefas.infraestructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final TarefaMapper tarefaMapper;
    private final JwtUtil jwtUtil;

    public TarefaDTO adicionarTarefa(TarefaDTO tarefaDTO, String token){
        tarefaDTO.setDataCriacao(LocalDateTime.now());
        tarefaDTO.setStatus(StatusNotificacaoEnum.PENDENTE);

        // Setando o e-mail do usuário autenticado
        tarefaDTO.setEmailUsuario(jwtUtil.extractUsername(getTokenFormatado(token)));
        Tarefa tarefaAdicionar = tarefaMapper.toTarefa(tarefaDTO);
        return tarefaMapper.toTarefaDTO(tarefaRepository.save(tarefaAdicionar));
    }

    // Retira a inicial e espaçamento do token
    public String getTokenFormatado(String token){
        String prefixoToken = "Bearer ";
        if(token.startsWith(prefixoToken)){
            token = token.substring(prefixoToken.length());
        }

        return token;
    }
}
