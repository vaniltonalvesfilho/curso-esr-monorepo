package com.algaworks.algafoodapi;

import com.algaworks.algafoodapi.core.io.Base64ProtocolResolver;
import com.algaworks.algafoodapi.infrastructure.repository.CustomJpaRepositoryImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
public class AlgafoodApiApplication {

	public static void main(String[] args) {
		// Configurando timezone da aplicação para UTC, boa prática
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		var app = new SpringApplication(AlgafoodApiApplication.class);
		app.addListeners(new Base64ProtocolResolver());
		app.run(args);
//		SpringApplication.run(AlgafoodApiApplication.class, args);
	}
	
}
