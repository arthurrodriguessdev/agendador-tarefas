package agendador.agendadortarefas.infraestructure.client;

import agendador.agendadortarefas.business.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

// Tudo que vem dentro de ${} é procurado nos arquivos de config do projeto, geralmente propperties
@FeignClient(name = "usuario", url = "${usuario.url}")
public interface UsuarioClient extends FeignClient {
    @GetMapping("/usuarios")
    UsuarioDTO buscarUsuarioPorEmail(@RequestParam("email") String email,
                                     @RequestHeader("Authorization") String token);
}
