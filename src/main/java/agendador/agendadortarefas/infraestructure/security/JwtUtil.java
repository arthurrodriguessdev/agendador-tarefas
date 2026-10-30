package agendador.agendadortarefas.infraestructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {
    @Value("${secretKey}")
    private String secretKey;

    private SecretKey getSecretKey(){
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    /** Extrai as informações declaradas no token JWT após validar sua assinatura. */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Retorna o identificador do usuário informado no assunto do token. */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /** Verifica se o token JWT já expirou. */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /** Valida se o token pertence ao usuário informado e ainda está vigente. */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
