package br.com.medfema.service;

import br.com.medfema.api.dto.*;
import br.com.medfema.api.dto.MedicoDtos.Cadastro;
import br.com.medfema.domain.Especialidade;
import br.com.medfema.exception.RegraNegocioException;
import br.com.medfema.repository.MedicoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {
    @Mock MedicoRepository repository;
    @InjectMocks MedicoService service;

    @Test
    void deveRecusarEmailDuplicado() {
        when(repository.existsByEmailIgnoreCase("medico@teste.com")).thenReturn(true);
        var endereco = new EnderecoDto("Rua A", "10", null, "Centro", "São Paulo", "SP", "01000-000");
        var dados = new Cadastro("Médico", "medico@teste.com", "11999999999", "12345", Especialidade.CARDIOLOGIA, endereco);
        assertThatThrownBy(() -> service.cadastrar(dados)).isInstanceOf(RegraNegocioException.class).hasMessageContaining("E-mail");
        verify(repository, never()).save(any());
    }
}
