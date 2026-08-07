package com.algaworks.algafoodapi.core.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.algaworks.algafoodapi.api.exceptionhandler.Problem;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Substitui a antiga configuração do SpringFox (removido: não é compatível com
 * Spring Boot 3 / Jakarta EE). Aqui os dois grupos de documentação (V1 e V2) são
 * declarados com {@link GroupedOpenApi} e as respostas globais são acrescentadas
 * por um {@link OpenApiCustomizer}.
 */
@Configuration
public class SpringDocConfig {

	private static final String SECURITY_SCHEME_NAME = "AlgaFood";

	private static final String PROBLEM_SCHEMA_REF = "#/components/schemas/Problema";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("AlgaFood API")
						.description("REST API para clientes e restaurantes.")
						.version("1.0")
						.contact(new Contact()
								.name("AlgaWorks")
								.url("https://www.algaworks.com")
								.email("contato@algaworks.com")))
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
				.addTagsItem(new Tag().name("Cidades").description("Gerencia as cidades"))
				.addTagsItem(new Tag().name("Grupos").description("Gerencia os grupos de usuários"))
				.addTagsItem(new Tag().name("Cozinhas").description("Gerencia as cozinhas"))
				.addTagsItem(new Tag().name("Formas Pagamento").description("Gerencia formas de pagamento"))
				.addTagsItem(new Tag().name("Pedidos").description("Gerencia os pedidos dos usuários"))
				.addTagsItem(new Tag().name("Restaurantes").description("Gerencia os restaurantes"))
				.addTagsItem(new Tag().name("Estados").description("Gerencia os estados"))
				.addTagsItem(new Tag().name("Produtos").description("Gerencia produtos"))
				.addTagsItem(new Tag().name("Usuários").description("Gerencia os usuários"))
				.addTagsItem(new Tag().name("Estatísticas").description("Estatísticas da AlgaFood"));
	}

	@Bean
	public GroupedOpenApi apiV1() {
		return GroupedOpenApi.builder()
				.group("V1")
				.pathsToMatch("/v1/**")
				.addOpenApiCustomizer(globalResponsesCustomizer())
				.build();
	}

	@Bean
	public GroupedOpenApi apiV2() {
		return GroupedOpenApi.builder()
				.group("V2")
				.pathsToMatch("/v2/**")
				.addOpenApiCustomizer(globalResponsesCustomizer())
				.build();
	}

	private SecurityScheme securityScheme() {
		var scopes = new Scopes()
				.addString("READ", "Acesso de leitura")
				.addString("WRITE", "Acesso de escrita");

		return new SecurityScheme()
				.type(SecurityScheme.Type.OAUTH2)
				.in(SecurityScheme.In.HEADER)
				.flows(new OAuthFlows()
						.authorizationCode(new OAuthFlow()
								.authorizationUrl("/oauth2/authorize")
								.tokenUrl("/oauth2/token")
								.scopes(scopes))
						.clientCredentials(new OAuthFlow()
								.tokenUrl("/oauth2/token")
								.scopes(scopes)));
	}

	/**
	 * Acrescenta em todas as operações as respostas de erro que antes eram
	 * declaradas via {@code globalResponses(...)} no Docket do SpringFox.
	 */
	private OpenApiCustomizer globalResponsesCustomizer() {
		return openApi -> {
			registerProblemSchema(openApi);

			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperationsMap()
				.forEach((httpMethod, operation) -> {
					var responses = operation.getResponses();

					switch (httpMethod) {
						case GET -> {
							addResponse(responses, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
							addResponse(responses, HttpStatus.NOT_ACCEPTABLE,
									"Recurso não possui representação que poderia ser aceita pelo consumidor");
						}
						case POST, PUT -> {
							addResponse(responses, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
							addResponse(responses, HttpStatus.NOT_ACCEPTABLE,
									"Recurso não possui representação que poderia ser aceita pelo consumidor");
							addResponse(responses, HttpStatus.BAD_REQUEST, "Requisição inválida (erro do cliente)");
							addResponse(responses, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
									"Requisição recusada porque o corpo está em um formato não suportado");
						}
						case DELETE -> {
							addResponse(responses, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
							addResponse(responses, HttpStatus.BAD_REQUEST, "Requisição inválida (erro do cliente)");
						}
						default -> {
						}
					}
				}));
		};
	}

	/**
	 * O schema de erro (Problema) não é o retorno de nenhum handler mapeado, então
	 * precisa ser adicionado manualmente em components para que as respostas globais
	 * não fiquem com um $ref quebrado.
	 */
	private void registerProblemSchema(OpenAPI openApi) {
		if (openApi.getComponents() == null) {
			openApi.setComponents(new Components());
		}

		if (openApi.getComponents().getSchemas() != null
				&& openApi.getComponents().getSchemas().containsKey("Problema")) {
			return;
		}

		ModelConverters.getInstance()
				.readAll(new AnnotatedType(Problem.class))
				.forEach(openApi.getComponents()::addSchemas);
	}

	private void addResponse(ApiResponses responses, HttpStatus status, String description) {
		var code = String.valueOf(status.value());

		if (responses.containsKey(code)) {
			return;
		}

		responses.addApiResponse(code, new ApiResponse()
				.description(description)
				.content(problemContent()));
	}

	private Content problemContent() {
		return new Content().addMediaType("application/json",
				new MediaType().schema(new Schema<>().$ref(PROBLEM_SCHEMA_REF)));
	}
}
