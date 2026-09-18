package br.com.medfema.api.dto;

import br.com.medfema.domain.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoDto(
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String uf,
        @NotBlank String cep) {
    public Endereco toEntity() {
        return Endereco.builder().logradouro(logradouro).numero(numero).complemento(complemento)
                .bairro(bairro).cidade(cidade).uf(uf.toUpperCase()).cep(cep).build();
    }

    public static EnderecoDto from(Endereco e) {
        return new EnderecoDto(e.getLogradouro(), e.getNumero(), e.getComplemento(), e.getBairro(), e.getCidade(), e.getUf(), e.getCep());
    }
}
