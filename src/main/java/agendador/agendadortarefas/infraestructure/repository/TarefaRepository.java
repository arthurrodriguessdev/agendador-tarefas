package agendador.agendadortarefas.infraestructure.repository;

import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {}