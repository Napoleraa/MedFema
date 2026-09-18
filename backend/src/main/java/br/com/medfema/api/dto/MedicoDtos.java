package br.com.medfema.api.dto;

import br.com.medfema.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public final class MedicoDtos {
    private MedicoDtos() {}

    public record Cadastro(@NotBlank String nome, @NotBlank @Email String email,
            @NotBlank String telefone, @NotBlank String crm, @NotNull Especialidade especialidade,
            @NotNull @Valid EnderecoDto endereco) {}

    public record Atualizacao(@NotBlank String nome, @NotBlank String telefone,
            @NotNull @Valid EnderecoDto endereco) {}

    public record Resposta(Long id, String nome, String email, String telefone, String crm,
            Especialidade especialidade, EnderecoDto endereco, boolean ativo) {
        public static Resposta from(Medico m) {
            return new Resposta(m.getId(), m.getNome(), m.getEmail(), m.getTelefone(), m.getCrm(),
                    m.getEspecialidade(), EnderecoDto.from(m.getEndereco()), m.isAtivo());
        }
    }
}
