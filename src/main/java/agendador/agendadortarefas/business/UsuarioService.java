package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.utils.TokenUtil;
import agendador.agendadortarefas.infraestructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final JwtUtil jwtUtil;

    String getEmailUsuario(String token) {
        return jwtUtil.extractUsername(TokenUtil.normalizarToken(token));
    }
}