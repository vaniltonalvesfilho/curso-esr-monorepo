package com.algaworks.algafoodapi.core.authorizationserver;

import java.time.Duration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * Recria, no formato do Spring Authorization Server, os clients que antes eram
 * inseridos em {@code oauth_client_details} pelo {@code afterMigrate.sql}.
 *
 * <p>Fazer isso em código (e não em SQL) evita ter que escrever à mão o JSON de
 * {@code client_settings}/{@code token_settings}, que é serializado pelo Jackson
 * do próprio Authorization Server.
 */
@Component
public class RegisteredClientSeeder implements ApplicationRunner {

	private final RegisteredClientRepository registeredClientRepository;

	private final PasswordEncoder passwordEncoder;

	public RegisteredClientSeeder(RegisteredClientRepository registeredClientRepository,
			PasswordEncoder passwordEncoder) {
		this.registeredClientRepository = registeredClientRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		salvar(algafoodWeb());
		salvar(foodanalytics());
		salvar(faturamento());
	}

	private void salvar(RegisteredClient registeredClient) {
		registeredClientRepository.save(registeredClient);
	}

	/**
	 * Antes usava o grant {@code password}, que não existe no OAuth 2.1. Virou um
	 * client público com authorization_code + PKCE.
	 *
	 * <p>Atenção: por ser público ({@code ClientAuthenticationMethod.NONE}), o
	 * {@code OAuth2RefreshTokenGenerator} do Spring Authorization Server nunca emite
	 * refresh token para ele — as configurações de refresh abaixo só passam a valer
	 * se o client virar confidencial. Um SPA renova a sessão repetindo o
	 * authorization_code com PKCE.
	 */
	private RegisteredClient algafoodWeb() {
		return RegisteredClient.withId("algafood-web")
				.clientId("algafood-web")
				.clientName("AlgaFood Web")
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://127.0.0.1:8080/authorized")
				// SPA Angular do monorepo (frontend/), servido pelo ng serve na 4200.
				.redirectUri("http://localhost:4200/authorized")
				.scope("READ")
				.scope("WRITE")
				.clientSettings(ClientSettings.builder()
						.requireProofKey(true)
						.requireAuthorizationConsent(false)
						.build())
				.tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofHours(6))
						.refreshTokenTimeToLive(Duration.ofDays(60))
						.reuseRefreshTokens(false)
						.build())
				.build();
	}

	private RegisteredClient foodanalytics() {
		return RegisteredClient.withId("foodanalytics")
				.clientId("foodanalytics")
				.clientName("Food Analytics")
				.clientSecret(passwordEncoder.encode("web123"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://aplicacao-cliente")
				.scope("READ")
				.scope("WRITE")
				.clientSettings(ClientSettings.builder()
						.requireProofKey(true)
						.requireAuthorizationConsent(true)
						.build())
				.build();
	}

	private RegisteredClient faturamento() {
		return RegisteredClient.withId("faturamento")
				.clientId("faturamento")
				.clientName("Faturamento")
				.clientSecret(passwordEncoder.encode("faturamento123"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("READ")
				.scope("WRITE")
				.clientSettings(ClientSettings.builder()
						.setting(AlgaFoodTokenCustomizer.CLIENT_AUTHORITIES_SETTING,
								"CONSULTAR_PEDIDOS,GERAR_RELATORIOS")
						.build())
				.build();
	}

}
