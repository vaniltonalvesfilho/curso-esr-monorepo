package com.algaworks.algafoodapi.core.authorizationserver;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Equivalente ao antigo {@code JwtCustomClaimsTokenEnhancer}: acrescenta ao access
 * token as claims usadas pela API ({@code usuario_id}, {@code nome_completo} e
 * {@code authorities}).
 */
@Component
public class AlgaFoodTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

	/** Chave usada em {@code ClientSettings} para as authorities de um client_credentials. */
	public static final String CLIENT_AUTHORITIES_SETTING = "settings.client.authorities";

	@Override
	public void customize(JwtEncodingContext context) {
		if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
			return;
		}

		var claims = context.getClaims();

		if (context.getPrincipal().getPrincipal() instanceof AuthUser authUser) {
			claims.claim("usuario_id", authUser.getUserId());
			claims.claim("nome_completo", authUser.getFullName());
			claims.claim("authorities", authoritiesDoUsuario(authUser));
		} else {
			claims.claim("authorities", authoritiesDoClient(context));
		}
	}

	private Set<String> authoritiesDoUsuario(AuthUser authUser) {
		return authUser.getAuthorities().stream()
				.map(authority -> authority.getAuthority().toUpperCase())
				.collect(Collectors.toSet());
	}

	/**
	 * No modelo antigo as authorities do client ficavam na coluna {@code authorities}
	 * de {@code oauth_client_details}. Aqui elas ficam em {@code ClientSettings}, como
	 * uma String separada por vírgulas (tipo final, seguro para o Jackson do
	 * Authorization Server).
	 */
	private Set<String> authoritiesDoClient(JwtEncodingContext context) {
		String authorities = context.getRegisteredClient()
				.getClientSettings()
				.getSetting(CLIENT_AUTHORITIES_SETTING);

		if (!StringUtils.hasText(authorities)) {
			return Set.of();
		}

		return Set.of(authorities.split("\\s*,\\s*"));
	}

}
