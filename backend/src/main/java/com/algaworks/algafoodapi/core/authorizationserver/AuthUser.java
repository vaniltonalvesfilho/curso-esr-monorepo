package com.algaworks.algafoodapi.core.authorizationserver;

import com.algaworks.algafoodapi.domain.model.Usuario;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Data
public class AuthUser extends User {
	
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String fullName;
		
	public AuthUser(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getEmail(), usuario.getSenha(), authorities);

		this.userId = usuario.getId();
		this.fullName = usuario.getNome();
	}

	/**
	 * Construtor usado pelo Jackson ao reidratar o principal que o
	 * {@code JdbcOAuth2AuthorizationService} grava como JSON na coluna
	 * {@code attributes} de {@code oauth2_authorization}. Ver {@link AuthUserMixin}.
	 */
	public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
			Long userId, String fullName) {
		super(username, password == null ? "" : password, authorities);

		this.userId = userId;
		this.fullName = fullName;
	}


	
}
