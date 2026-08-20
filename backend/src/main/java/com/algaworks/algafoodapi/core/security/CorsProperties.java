package com.algaworks.algafoodapi.core.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("algafood.cors")
public class CorsProperties {

	/**
	 * Origens que podem chamar a API pelo browser.
	 *
	 * <p>Precisa ser a lista exata, e não {@code *}: o login do SPA manda o cookie
	 * de sessão junto ({@code credentials: 'include'}), e o browser recusa
	 * {@code Access-Control-Allow-Origin: *} em requisição com credencial. Um
	 * curinga aqui também deixaria qualquer site fazer chamada autenticada à API
	 * com a sessão de quem estivesse logado.
	 */
	@NotEmpty
	private List<String> allowedOrigins = List.of("http://localhost:4200");

}
