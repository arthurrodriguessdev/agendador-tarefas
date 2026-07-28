package agendador.agendadortarefas.infraestructure.entity;

import agendador.agendadortarefas.infraestructure.enums.StatusNotificacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "tarefa")
@Builder
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_tarefa", length = 100)
    private String nomeTarefa;
    @Column(name = "descricao_tarefa")
    private String descricaoTarefa;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    @Column(name = "data_evento")
    private LocalDateTime dataEvento;
    @Column(name = "email_usuario")
    private String emailUsuario;
    @Column(name = "status")
    private StatusNotificacaoEnum status;
}