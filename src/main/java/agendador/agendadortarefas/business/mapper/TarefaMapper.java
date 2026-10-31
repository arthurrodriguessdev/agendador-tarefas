package agendador.agendadortarefas.business.mapper;

import agendador.agendadortarefas.business.dto.tarefa.TarefaAdicionarDTO;
import agendador.agendadortarefas.business.dto.tarefa.TarefaResponseDTO;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TarefaMapper {
    @Mapping(target = "id", ignore = true)
    Tarefa toTarefa(TarefaAdicionarDTO tarefaAdicionarDTO);

    List<TarefaResponseDTO> toTarefasResponseDTO(List<Tarefa> listaTarefas);
    TarefaResponseDTO toTarefaResponseDTO(Tarefa tarefa);
}