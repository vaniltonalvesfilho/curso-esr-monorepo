package com.algaworks.algafoodapi.core.security;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Configuração do resource server. No Spring Security 6 o
 * {@code WebSecurityConfigurerAdapter} não existe mais: a cadeia de filtros é
 * declarada como um bean {@link SecurityFilterChain}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

	/**
	 * Marca que o SPA usa para pedir uma resposta de status em vez do redirect da
	 * página de login.
	 *
	 * <p>É preferível ao {@code Accept} porque não é ambíguo: o
	 * {@code application/json, text/plain, *&#47;*} que o Angular manda por padrão
	 * casa tanto com JSON quanto com HTML. Por não estar na safelist de CORS, ele
	 * também força um preflight — o que impede um site qualquer de disparar um
	 * POST de login cross-origin como requisição simples, já que o CSRF está
	 * desligado nesta cadeia.
	 */
	private static final String CABECALHO_XHR = "X-Requested-With";

	@Bean
	public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
		// Comportamento de browser: redirect para a página de login e de volta para
		// a requisição salva. É o que o Swagger UI e o Bruno usam.
		var sucessoDeBrowser = new SavedRequestAwareAuthenticationSuccessHandler();
		var falhaDeBrowser = new SimpleUrlAuthenticationFailureHandler("/login?error");
		var logoutDeBrowser = new SimpleUrlLogoutSuccessHandler();

		http
			.authorizeHttpRequests(authorize -> authorize
					.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
					.anyRequest().authenticated())
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.formLogin(form -> form
					.successHandler(sucessoDeLogin(sucessoDeBrowser))
					.failureHandler(falhaDeLogin(falhaDeBrowser)))
			.logout(logout -> logout
					.logoutSuccessHandler(sucessoDeLogout(logoutDeBrowser)))
			.oauth2ResourceServer(oauth2 -> oauth2
					.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		return http.build();
	}

	/**
	 * O SPA autentica com um POST em {@code /login} para abrir a sessão e só então
	 * dispara o {@code /oauth2/authorize}, que já devolve o code direto. Para isso
	 * ele precisa de um 204, não de um redirect que o {@code fetch} seguiria em
	 * silêncio até uma página HTML.
	 */
	private AuthenticationSuccessHandler sucessoDeLogin(AuthenticationSuccessHandler paraBrowser) {
		return (request, response, authentication) -> {
			if (ehChamadaDeSpa(request)) {
				response.setStatus(HttpStatus.NO_CONTENT.value());
				return;
			}

			paraBrowser.onAuthenticationSuccess(request, response, authentication);
		};
	}

	private AuthenticationFailureHandler falhaDeLogin(AuthenticationFailureHandler paraBrowser) {
		return (request, response, exception) -> {
			if (ehChamadaDeSpa(request)) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				// Mensagem fixa de propósito: distinguir "usuário não existe" de
				// "senha errada" entrega uma lista de e-mails válidos a quem tentar.
				response.getWriter().write("{\"mensagem\":\"E-mail ou senha inválidos.\"}");
				return;
			}

			paraBrowser.onAuthenticationFailure(request, response, exception);
		};
	}

	private LogoutSuccessHandler sucessoDeLogout(LogoutSuccessHandler paraBrowser) {
		return (request, response, authentication) -> {
			if (ehChamadaDeSpa(request)) {
				response.setStatus(HttpStatus.NO_CONTENT.value());
				return;
			}

			paraBrowser.onLogoutSuccess(request, response, authentication);
		};
	}

	private static boolean ehChamadaDeSpa(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader(CABECALHO_XHR));
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		var jwtAuthenticationConverter = new JwtAuthenticationConverter();

		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			var authorities = jwt.getClaimAsStringList("authorities");

			if (authorities == null) {
				authorities = Collections.emptyList();
			}

			var scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
			Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);

			grantedAuthorities.addAll(authorities.stream()
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList()));

			return grantedAuthorities;
		});

		return jwtAuthenticationConverter;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}
