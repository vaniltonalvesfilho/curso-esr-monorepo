package com.algaworks.algafoodapi.core.authorizationserver;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Authorization Server baseado no Spring Authorization Server.
 *
 * <p>Substitui o antigo {@code spring-security-oauth2} (projeto em fim de vida e
 * incompatível com Spring Boot 3). Consequências da troca:
 * <ul>
 *   <li>os endpoints passaram de {@code /oauth/token} e {@code /oauth/authorize}
 *       para {@code /oauth2/token} e {@code /oauth2/authorize};</li>
 *   <li>o JWK Set é publicado pelo próprio servidor em {@code /oauth2/jwks} — o
 *       antigo {@code JwkSetController} deixou de ser necessário;</li>
 *   <li>PKCE é nativo, então o {@code PkceAuthorizationCodeTokenGranter} foi removido;</li>
 *   <li>o grant {@code password} não existe mais no OAuth 2.1 e foi substituído por
 *       {@code authorization_code} + PKCE no cliente {@code algafood-web}.</li>
 * </ul>
 */
@Configuration
public class AuthorizationServerConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		var authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			.with(authorizationServerConfigurer, authorizationServer -> authorizationServer
					.oidc(Customizer.withDefaults()))
			.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
			.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

		return http.build();
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
		return new JdbcRegisteredClientRepository(jdbcOperations);
	}

	/**
	 * O principal autenticado é persistido como JSON na coluna {@code attributes}.
	 * O {@code ObjectMapper} default só conhece as classes da allowlist do
	 * {@code SecurityJackson2Modules}, então o {@link AuthUser} precisa entrar via
	 * mixin — sem isso o token endpoint devolve 500 no fluxo authorization_code.
	 */
	@Bean
	public OAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		var objectMapper = new ObjectMapper();
		objectMapper.registerModules(
				SecurityJackson2Modules.getModules(JdbcOAuth2AuthorizationService.class.getClassLoader()));
		objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
		objectMapper.addMixIn(AuthUser.class, AuthUserMixin.class);

		var rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
		rowMapper.setObjectMapper(objectMapper);

		var parametersMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper();
		parametersMapper.setObjectMapper(objectMapper);

		var authorizationService = new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
		authorizationService.setAuthorizationRowMapper(rowMapper);
		authorizationService.setAuthorizationParametersMapper(parametersMapper);

		return authorizationService;
	}

	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registeredClientRepository);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(JwtKeyStoreProperties properties) {
		var keyPair = keyPair(properties);

		var rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
				.privateKey((RSAPrivateKey) keyPair.getPrivate())
				.keyID("algafood-key-id")
				.build();

		return new ImmutableJWKSet<>(new JWKSet(rsaKey));
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
	 * Carrega o par de chaves do keystore. O {@code KeyStoreKeyFactory} usado antes
	 * vinha do spring-security-oauth2, que não existe mais, então a leitura passa a
	 * ser feita direto pela API do JDK.
	 */
	private KeyPair keyPair(JwtKeyStoreProperties properties) {
		// Um placeholder não resolvido chega aqui como o texto literal "${VAR}", que
		// passa no @NotBlank e só quebraria na leitura do keystore, com uma mensagem
		// sugerindo arquivo corrompido. Vale mais apontar a variável que falta.
		conferirPlaceholderResolvido("algafood.jwt.keystore.jks-location",
				properties.getJksLocation().getDescription());
		conferirPlaceholderResolvido("algafood.jwt.keystore.password", properties.getPassword());
		conferirPlaceholderResolvido("algafood.jwt.keystore.keypair-alias", properties.getKeypairAlias());

		var password = properties.getPassword().toCharArray();

		try (var inputStream = properties.getJksLocation().getInputStream()) {
			var keyStore = KeyStore.getInstance("JKS");
			keyStore.load(inputStream, password);

			var privateKey = (PrivateKey) keyStore.getKey(properties.getKeypairAlias(), password);
			var certificate = keyStore.getCertificate(properties.getKeypairAlias());

			return new KeyPair(certificate.getPublicKey(), privateKey);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Não foi possível carregar o par de chaves de " + properties.getJksLocation(), e);
		}
	}

	private void conferirPlaceholderResolvido(String propriedade, String valor) {
		if (valor != null && valor.contains("${")) {
			throw new IllegalStateException("A propriedade " + propriedade + " ficou com o placeholder "
					+ "não resolvido (" + valor + "). Defina a variável de ambiente correspondente.");
		}
	}

}
