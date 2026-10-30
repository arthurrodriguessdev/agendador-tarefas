package agendador.agendadortarefas.business.utils;

public class TokenUtil {
    /**
     * Formata o token excluindo a parte inicial "Bearer"
     *
     * @param token accessToken do usuário
     */
    public static String normalizarToken(String token){
        String prefixoExcluir = "Bearer ";
        if(token.startsWith(prefixoExcluir)){
            token = token.substring(prefixoExcluir.length());
        }

        return token;
    }
}