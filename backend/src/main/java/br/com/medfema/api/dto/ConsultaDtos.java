package br.com.medfema.api.dto;

import br.com.medfema.domain.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class ConsultaDtos {
    private ConsultaDtos() {}

    public record Agendamento(@NotNull Long pacienteId, Long medicoId,
            Especialidade especialidade, @NotNull @Future LocalDateTime dataHora) {}

    public record Cancelamento(@NotNull MotivoCancelamento motivo) {}

    public record Resposta(Long id, Long medicoId, String medico, Long pacienteId, String paciente,
            LocalDateTime dataHora, boolean cancelada, MotivoCancelamento motivoCancelamento) {
        public static Resposta from(Consulta c) {
            return new Resposta(c.getId(), c.getMedico().getId(), c.getMedico().getNome(),
                    c.getPaciente().getId(), c.getPaciente().getNome(), c.getDataHora(),
                    c.isCancelada(), c.getMotivoCancelamento());
        }
    }
}
