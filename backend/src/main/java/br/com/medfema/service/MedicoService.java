package br.com.medfema.service;

import br.com.medfema.api.dto.MedicoDtos.*;
import br.com.medfema.domain.Medico;
import br.com.medfema.exception.RegraNegocioException;
import br.com.medfema.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MedicoService {
    private final MedicoRepository repository;

    @Transactional
    public Resposta cadastrar(Cadastro dados) {
        if (repository.existsByEmailIgnoreCase(dados.email())) throw new RegraNegocioException("E-mail já cadastrado");
        if (repository.existsByCrmIgnoreCase(dados.crm())) throw new RegraNegocioException("CRM já cadastrado");
        var medico = Medico.builder().nome(dados.nome()).email(dados.email().toLowerCase())
                .telefone(dados.telefone()).crm(dados.crm()).especialidade(dados.especialidade())
                .endereco(dados.endereco().toEntity()).ativo(true).build();
        return Resposta.from(repository.save(medico));
    }

    @Transactional(readOnly = true)
    public Page<Resposta> listar(int pagina) {
        return repository.findByAtivoTrue(PageRequest.of(pagina, 10, Sort.by("nome").ascending())).map(Resposta::from);
    }

    @Transactional(readOnly = true)
    public Resposta detalhar(Long id) { return Resposta.from(buscar(id)); }

    @Transactional
    public Resposta atualizar(Long id, Atualizacao dados) {
        var medico = buscar(id);
        medico.atualizar(dados.nome(), dados.telefone(), dados.endereco().toEntity());
        return Resposta.from(medico);
    }

    @Transactional
    public void excluir(Long id) { buscar(id).inativar(); }

    private Medico buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Médico não encontrado"));
    }
}
