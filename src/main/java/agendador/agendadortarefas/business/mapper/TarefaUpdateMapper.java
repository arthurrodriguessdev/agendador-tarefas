package agendador.agendadortarefas.business.mapper;

import agendador.agendadortarefas.business.dto.TarefaDTO;
import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

// Se o campo for nulo, pega de tarefa. Caso não seja, pega os dados do DTO
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TarefaUpdateMapper {
    void update(TarefaDTO tarefaDTO, @MappingTarget Tarefa tarefa);
}