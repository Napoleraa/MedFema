package br.com.medfema.repository;

import br.com.medfema.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findByAtivoTrue(Pageable pageable);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCrmIgnoreCase(String crm);
    List<Medico> findByAtivoTrueAndEspecialidade(Especialidade especialidade);
}
