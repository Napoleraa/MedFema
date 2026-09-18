package br.com.medfema.repository;

import br.com.medfema.domain.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    boolean existsByMedicoIdAndDataHoraAndCanceladaFalse(Long medicoId, LocalDateTime dataHora);
    boolean existsByPacienteIdAndDataHoraBetweenAndCanceladaFalse(Long pacienteId, LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findAllByOrderByDataHoraAsc();
}
