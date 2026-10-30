package agendador.agendadortarefas.business;

import agendador.agendadortarefas.business.utils.TokenUtil;
import agendador.agendadortarefas.infraestructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioService {
    private final JwtUtil jwtUtil;

    public String getEmailUsuario(String token) {
        return jwtUtil.extractUsername(getTokenNormalizado(token));
    }

    private String getTokenNormalizado(String token){
        return TokenUtil.normalizarToken(token);
    }
}