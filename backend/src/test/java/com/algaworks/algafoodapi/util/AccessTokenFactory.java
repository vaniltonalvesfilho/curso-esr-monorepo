package com.algaworks.algafoodapi.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Emite access tokens para os testes de integração.
 *
 * <p>A API é um resource server: sem {@code Authorization: Bearer} toda requisição
 * volta 401. Passar pelo fluxo do authorization server dentro do teste exigiria
 * authorization_code + PKCE (o grant {@code password} não existe mais no OAuth 2.1),
 * então o token é assinado aqui com o mesmo par de chaves do {@code JWKSource} da
 * aplicação — o {@code JwtDecoder} valida a assinatura da mesma forma.
 */
@Component
public class AccessTokenFactory {

	private final NimbusJwtEncoder jwtEncoder;

	public AccessTokenFactory(JWKSource<SecurityContext> jwkSource) {
		this.jwtEncoder = new NimbusJwtEncoder(jwkSource);
	}

	/**
	 * Token de um usuário com os escopos READ e WRITE mais as authorities informadas.
	 *
	 * @param authorities as permissões esperadas pelas anotações {@code @CheckSecurity}
	 *                    do endpoint sob teste (ex.: {@code EDITAR_COZINHAS}).
	 */
	public String gerarToken(String... authorities) {
		var agora = Instant.now();

		var claims = JwtClaimsSet.builder()
				.subject("teste@algafood.local")
				.issuedAt(agora)
				.expiresAt(agora.plus(1, ChronoUnit.HOURS))
				.claim("scope", List.of("READ", "WRITE"))
				.claim("authorities", List.of(authorities))
				.claim("usuario_id", 1L)
				.claim("nome_completo", "Usuário de Teste")
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
