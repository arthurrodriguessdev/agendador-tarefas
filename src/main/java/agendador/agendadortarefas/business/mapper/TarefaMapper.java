package agendador.agendadortarefas.business.mapper;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import org.mapstruct.Mapper;
import java.util.List;

/*
* Responsável pelo mapeamento entre classes (faz uma conversão)
* - Substitui o builder
* */
@Mapper(componentModel = "spring")
public interface TarefaMapper {
    TarefaDTO toTarefaDTO(Tarefa tarefa);
    Tarefa toTarefa(TarefaDTO tarefaDTO);
    List<TarefaDTO> toTarefasDTO(List<Tarefa> listaTarefas);
}