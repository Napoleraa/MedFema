package br.com.medfema.api.dto;

import br.com.medfema.domain.Paciente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public final class PacienteDtos {
    private PacienteDtos() {}

    public record Cadastro(@NotBlank String nome, @NotBlank @Email String email,
            @NotBlank String telefone, @NotBlank String cpf, @NotNull @Valid EnderecoDto endereco) {}

    public record Atualizacao(@NotBlank String nome, @NotBlank String telefone,
            @NotNull @Valid EnderecoDto endereco) {}

    public record Resposta(Long id, String nome, String email, String telefone, String cpf,
            EnderecoDto endereco, boolean ativo) {
        public static Resposta from(Paciente p) {
            return new Resposta(p.getId(), p.getNome(), p.getEmail(), p.getTelefone(), p.getCpf(),
                    EnderecoDto.from(p.getEndereco()), p.isAtivo());
        }
    }
}
