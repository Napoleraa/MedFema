package br.com.medfema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "MedFEMA API", version = "1.0", description = "API da primeira release do sistema MedFEMA"))
public class MedfemaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedfemaApiApplication.class, args);
	}

}
