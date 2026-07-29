package agendador.agendadortarefas.infraestructure.repository;

import agendador.agendadortarefas.infraestructure.entity.Tarefa;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByEmailUsuario(String email);

    /*
    * Escrevendo consulta por meio do SQL nativo.
    * - @Param faz a ligação do parâmetro nomeado com a consulta SQL
    * - ":" substitui o valor do parâmetro nomeado
    * */
    @Query(value =
            """
            SELECT * FROM tarefa
            WHERE data_evento BETWEEN :dataInicio AND :dataFim
            """,
            nativeQuery = true
    )
    List<Tarefa> buscarTarefasPorIntervaloDatas(@Param LocalDateTime dataInicio, @Param LocalDateTime dataFim);
}