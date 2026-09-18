package br.com.medfema.service;

import br.com.medfema.api.dto.ConsultaDtos.Agendamento;
import br.com.medfema.domain.Especialidade;
import br.com.medfema.exception.RegraNegocioException;
import br.com.medfema.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {
    @Mock ConsultaRepository consultas;
    @Mock MedicoRepository medicos;
    @Mock PacienteRepository pacientes;
    @InjectMocks ConsultaService service;

    @Test
    void deveRecusarConsultaNoDomingo() {
        var domingo = LocalDate.now().plusWeeks(2).with(DayOfWeek.SUNDAY).atTime(10, 0);
        var dados = new Agendamento(1L, null, Especialidade.CARDIOLOGIA, domingo);
        assertThatThrownBy(() -> service.agendar(dados)).isInstanceOf(RegraNegocioException.class).hasMessageContaining("segunda a sábado");
    }
}
