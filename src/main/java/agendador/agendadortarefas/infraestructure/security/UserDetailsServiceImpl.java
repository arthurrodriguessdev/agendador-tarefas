package agendador.agendadortarefas.infraestructure.security;

import agendador.agendadortarefas.business.dto.UsuarioDTO;
import agendador.agendadortarefas.infraestructure.client.UsuarioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl {
    private final UsuarioClient usuarioClient;

    public UserDetails carregarDadosUsuario(String email, String token){
        UsuarioDTO usuario = usuarioClient.buscarUsuarioPorEmail(email, token);
        return User.withUsername(usuario.email())
                .password(usuario.senha())
                .build();
    }
}