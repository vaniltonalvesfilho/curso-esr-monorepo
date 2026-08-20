package com.algaworks.algafoodapi.core.authorizationserver;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Ensina o Jackson do Spring Security a serializar e desserializar {@link AuthUser}.
 *
 * <p>O {@code JdbcOAuth2AuthorizationService} grava o principal autenticado como
 * JSON na coluna {@code attributes} de {@code oauth2_authorization}. Para evitar
 * desserialização de tipos arbitrários, o {@code SecurityJackson2Modules} só aceita
 * classes de uma allowlist — sem este mixin o token endpoint falha com
 * {@code IllegalArgumentException: ... is not in the allowlist}.
 *
 * <p>Espelha o {@code UserMixin} do próprio Spring Security: os campos (e não os
 * getters gerados pelo Lombok) são a fonte do JSON, e o {@code @class} embutido
 * permite reconstruir o tipo concreto na leitura.
 *
 * <p>O {@code @JsonDeserialize} com {@code None} é essencial: o Jackson resolve
 * anotações de classe subindo a hierarquia, então {@code AuthUser} herdaria o
 * {@code @JsonDeserialize(using = UserDeserializer.class)} do {@code UserMixin} —
 * que devolve um {@link org.springframework.security.core.userdetails.User} puro e
 * descarta {@code userId}/{@code fullName}. O resultado era um access token sem as
 * claims {@code usuario_id}, {@code nome_completo} e {@code authorities}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		isGetterVisibility = Visibility.NONE)
@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AuthUserMixin {

	@JsonCreator
	AuthUserMixin(@JsonProperty("username") String username,
			@JsonProperty("password") String password,
			@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
			@JsonProperty("userId") Long userId,
			@JsonProperty("fullName") String fullName) {
	}

}
