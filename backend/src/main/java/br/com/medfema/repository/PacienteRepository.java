package br.com.medfema.repository;

import br.com.medfema.domain.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Page<Paciente> findByAtivoTrue(Pageable pageable);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
}
