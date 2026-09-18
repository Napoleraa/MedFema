package br.com.medfema.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    @Column(columnDefinition = "CHAR(2)")
    private String uf;
    private String cep;

    public void atualizar(Endereco novo) {
        this.logradouro = novo.logradouro;
        this.numero = novo.numero;
        this.complemento = novo.complemento;
        this.bairro = novo.bairro;
        this.cidade = novo.cidade;
        this.uf = novo.uf;
        this.cep = novo.cep;
    }
}
