package br.com.medfema.service;

import br.com.medfema.api.dto.PacienteDtos.*;
import br.com.medfema.domain.Paciente;
import br.com.medfema.exception.RegraNegocioException;
import br.com.medfema.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository repository;

    @Transactional
    public Resposta cadastrar(Cadastro dados) {
        if (repository.existsByEmailIgnoreCase(dados.email())) throw new RegraNegocioException("E-mail já cadastrado");
        if (repository.existsByCpf(dados.cpf())) throw new RegraNegocioException("CPF já cadastrado");
        var paciente = Paciente.builder().nome(dados.nome()).email(dados.email().toLowerCase())
                .telefone(dados.telefone()).cpf(dados.cpf()).endereco(dados.endereco().toEntity())
                .ativo(true).build();
        return Resposta.from(repository.save(paciente));
    }

    @Transactional(readOnly = true)
    public Page<Resposta> listar(int pagina) {
        return repository.findByAtivoTrue(PageRequest.of(pagina, 10, Sort.by("nome").ascending())).map(Resposta::from);
    }

    @Transactional(readOnly = true)
    public Resposta detalhar(Long id) { return Resposta.from(buscar(id)); }

    @Transactional
    public Resposta atualizar(Long id, Atualizacao dados) {
        var paciente = buscar(id);
        paciente.atualizar(dados.nome(), dados.telefone(), dados.endereco().toEntity());
        return Resposta.from(paciente);
    }

    @Transactional
    public void excluir(Long id) { buscar(id).inativar(); }

    private Paciente buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Paciente não encontrado"));
    }
}
