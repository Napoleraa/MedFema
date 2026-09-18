package br.com.medfema.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey chave;
    private final long expiracao;

    public JwtService(@Value("${app.jwt.secret}") String segredo, @Value("${app.jwt.expiration-ms:3600000}") long expiracao) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracao = expiracao;
    }

    public String gerar(String usuario) {
        var agora = Instant.now();
        return Jwts.builder().subject(usuario).issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(expiracao))).signWith(chave).compact();
    }

    public String validarEObterUsuario(String token) {
        return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
