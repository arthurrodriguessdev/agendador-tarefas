package agendador.agendadortarefas.business.dto;

/*
* DTO criado para pegar os dados que vão vir da requisição para a API UsuarioClient
* - NÃO se compartilha DTO, cada aplicação tem o seu para a armazenagem de dados
* */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private String email;
    private String senha;
}
