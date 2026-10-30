package agendador.agendadortarefas.business.utils;

public class TokenUtil {
    /**
     * Remove o prefixo "Bearer " do token
     *
     * @param token token recebido no cabeçalho Authorization
     * @return token sem o prefixo "Bearer "
     */
    public static String normalizarToken(String token){
        String prefixoExcluir = "Bearer ";
        if(token.startsWith(prefixoExcluir)){
            token = token.substring(prefixoExcluir.length());
        }

        return token;
    }
}