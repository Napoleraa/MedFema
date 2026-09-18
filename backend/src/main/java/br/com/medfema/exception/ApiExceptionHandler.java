package br.com.medfema.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record Erro(Instant instante, int status, String mensagem, List<String> detalhes) {}

    @ExceptionHandler(RegraNegocioException.class)
    ResponseEntity<Erro> regra(RegraNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Erro> validacao(MethodArgumentNotValidException ex) {
        var detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + Optional.ofNullable(e.getDefaultMessage()).orElse("inválido"))
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", detalhes);
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Erro> naoEncontrado(NoSuchElementException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Erro> inesperado(Exception ex) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado", List.of());
    }

    private ResponseEntity<Erro> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status).body(new Erro(Instant.now(), status.value(), mensagem, detalhes));
    }
}
