package agendador.agendadortarefas.infraestructure.repository;

import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByEmailUsuario(String email);
    List<Tarefa> findByEmailUsuarioAndDataEventoBetween(
            String email, LocalDateTime dataInicio, LocalDateTime dataFim);
    Optional<Tarefa> findByIdAndEmailUsuario(Long id, String email);
}