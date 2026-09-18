package br.com.medfema.api;

import br.com.medfema.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public record Login(@NotBlank String usuario, @NotBlank String senha) {}
    public record Token(String token, String tipo) {}

    @PostMapping("/login")
    Token login(@RequestBody @Valid Login dados) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dados.usuario(), dados.senha()));
        return new Token(jwtService.gerar(dados.usuario()), "Bearer");
    }
}
