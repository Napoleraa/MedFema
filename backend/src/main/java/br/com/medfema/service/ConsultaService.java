package br.com.medfema.service;

import br.com.medfema.api.dto.ConsultaDtos.*;
import br.com.medfema.domain.*;
import br.com.medfema.exception.RegraNegocioException;
import br.com.medfema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConsultaService {
    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final Random random = new Random();

    @Transactional
    public Resposta agendar(Agendamento dados) {
        validarHorario(dados.dataHora());
        var paciente = pacienteRepository.findById(dados.pacienteId())
                .orElseThrow(() -> new NoSuchElementException("Paciente não encontrado"));
        if (!paciente.isAtivo()) throw new RegraNegocioException("Não é possível agendar para paciente inativo");
        var inicio = dados.dataHora().toLocalDate().atStartOfDay();
        if (consultaRepository.existsByPacienteIdAndDataHoraBetweenAndCanceladaFalse(paciente.getId(), inicio, inicio.plusDays(1).minusNanos(1)))
            throw new RegraNegocioException("O paciente já possui consulta neste dia");

        var medico = escolherMedico(dados);
        var consulta = Consulta.builder().medico(medico).paciente(paciente).dataHora(dados.dataHora()).cancelada(false).build();
        return Resposta.from(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public List<Resposta> listar() { return consultaRepository.findAllByOrderByDataHoraAsc().stream().map(Resposta::from).toList(); }

    @Transactional
    public Resposta cancelar(Long id, Cancelamento dados) {
        var consulta = consultaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Consulta não encontrada"));
        if (consulta.isCancelada()) throw new RegraNegocioException("A consulta já foi cancelada");
        if (LocalDateTime.now().plusHours(24).isAfter(consulta.getDataHora()))
            throw new RegraNegocioException("A consulta só pode ser cancelada com 24 horas de antecedência");
        consulta.cancelar(dados.motivo());
        return Resposta.from(consulta);
    }

    private Medico escolherMedico(Agendamento dados) {
        if (dados.medicoId() != null) {
            var medico = medicoRepository.findById(dados.medicoId()).orElseThrow(() -> new NoSuchElementException("Médico não encontrado"));
            if (!medico.isAtivo()) throw new RegraNegocioException("Não é possível agendar com médico inativo");
            if (consultaRepository.existsByMedicoIdAndDataHoraAndCanceladaFalse(medico.getId(), dados.dataHora()))
                throw new RegraNegocioException("O médico já possui consulta neste horário");
            return medico;
        }
        if (dados.especialidade() == null) throw new RegraNegocioException("Informe a especialidade quando não escolher um médico");
        var disponiveis = medicoRepository.findByAtivoTrueAndEspecialidade(dados.especialidade()).stream()
                .filter(m -> !consultaRepository.existsByMedicoIdAndDataHoraAndCanceladaFalse(m.getId(), dados.dataHora())).toList();
        if (disponiveis.isEmpty()) throw new RegraNegocioException("Nenhum médico disponível para a data e especialidade informadas");
        return disponiveis.get(random.nextInt(disponiveis.size()));
    }

    private void validarHorario(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(30)))
            throw new RegraNegocioException("Agende com pelo menos 30 minutos de antecedência");
        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY || dataHora.toLocalTime().isBefore(LocalTime.of(7, 0))
                || !dataHora.toLocalTime().isBefore(LocalTime.of(19, 0)))
            throw new RegraNegocioException("A clínica atende de segunda a sábado, das 07:00 às 19:00");
    }
}
