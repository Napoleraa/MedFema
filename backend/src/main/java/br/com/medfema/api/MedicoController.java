package br.com.medfema.api;

import br.com.medfema.api.dto.MedicoDtos.*;
import br.com.medfema.service.MedicoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
@Tag(name = "Médicos")
public class MedicoController {
    private final MedicoService service;

    @PostMapping
    ResponseEntity<Resposta> cadastrar(@RequestBody @Valid Cadastro dados) {
        var resposta = service.cadastrar(dados);
        return ResponseEntity.created(URI.create("/api/medicos/" + resposta.id())).body(resposta);
    }
    @GetMapping Page<Resposta> listar(@RequestParam(defaultValue = "0") int pagina) { return service.listar(pagina); }
    @GetMapping("/{id}") Resposta detalhar(@PathVariable Long id) { return service.detalhar(id); }
    @PutMapping("/{id}") Resposta atualizar(@PathVariable Long id, @RequestBody @Valid Atualizacao dados) { return service.atualizar(id, dados); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void excluir(@PathVariable Long id) { service.excluir(id); }
}
