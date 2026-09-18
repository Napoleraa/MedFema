package br.com.medfema.api;

import br.com.medfema.api.dto.ConsultaDtos.*;
import br.com.medfema.service.ConsultaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas")
public class ConsultaController {
    private final ConsultaService service;

    @PostMapping
    ResponseEntity<Resposta> agendar(@RequestBody @Valid Agendamento dados) {
        var resposta = service.agendar(dados);
        return ResponseEntity.created(URI.create("/api/consultas/" + resposta.id())).body(resposta);
    }
    @GetMapping List<Resposta> listar() { return service.listar(); }
    @DeleteMapping("/{id}") Resposta cancelar(@PathVariable Long id, @RequestBody @Valid Cancelamento dados) {
        return service.cancelar(id, dados);
    }
}
