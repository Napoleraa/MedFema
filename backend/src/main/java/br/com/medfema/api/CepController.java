package br.com.medfema.api;

import br.com.medfema.service.CepService;
import br.com.medfema.service.CepService.EnderecoCep;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cep")
@RequiredArgsConstructor
@Tag(name = "CEP")
public class CepController {
    private final CepService service;

    @GetMapping("/{cep}")
    EnderecoCep buscar(@PathVariable String cep) {
        return service.buscar(cep);
    }
}
