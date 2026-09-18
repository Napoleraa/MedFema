package br.com.medfema.service;

import br.com.medfema.exception.RegraNegocioException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CepService {
    private final RestClient restClient;

    public CepService() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(5_000);
        this.restClient = RestClient.builder()
                .baseUrl("https://viacep.com.br/ws")
                .requestFactory(requestFactory)
                .build();
    }

    public EnderecoCep buscar(String valor) {
        var cep = valor == null ? "" : valor.replaceAll("\\D", "");
        if (!cep.matches("\\d{8}")) {
            throw new RegraNegocioException("O CEP deve conter exatamente 8 números");
        }
        try {
            var resposta = restClient.get().uri("/{cep}/json/", cep).retrieve().body(ViaCepResposta.class);
            if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
                throw new RegraNegocioException("CEP não encontrado");
            }
            return new EnderecoCep(resposta.cep(), resposta.logradouro(), resposta.complemento(),
                    resposta.bairro(), resposta.localidade(), resposta.uf());
        } catch (RegraNegocioException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new RegraNegocioException("Não foi possível consultar o CEP agora. Tente novamente");
        }
    }

    public record EnderecoCep(String cep, String logradouro, String complemento,
                              String bairro, String cidade, String uf) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ViaCepResposta(String cep, String logradouro, String complemento,
                                  String bairro, String localidade, String uf, Boolean erro) {}
}
