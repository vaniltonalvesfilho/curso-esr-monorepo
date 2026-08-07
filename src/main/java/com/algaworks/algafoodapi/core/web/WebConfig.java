package com.algaworks.algafoodapi.core.web;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// Precisa rodar depois do WebMvcConfigurer do spring-hateoas para enxergar o
// conversor HAL já registrado.
@Order(Ordered.LOWEST_PRECEDENCE)
public class WebConfig implements WebMvcConfigurer {

	/**
	 * Mantém o media type versionado como preferência default, mas com
	 * {@code application/json} como fallback: sem ele, toda requisição sem cabeçalho
	 * {@code Accept} (ou com {@code Accept: *}{@code /*}) recebia 406, porque os
	 * controllers declaram {@code produces = application/json}. É o caso do
	 * Swagger UI, que busca {@code /v3/api-docs} sem negociar media type.
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(AlgaMediaTypes.V2_APPLICATION_JSON, MediaType.APPLICATION_JSON);
	}

	/**
	 * Faz o conversor HAL também responder pelos media types versionados da API.
	 *
	 * <p>Substitui o antigo {@code HalCustomMediaTypeEnabler}, que mexia na lista de
	 * conversores do {@code RequestMappingHandlerAdapter} dentro de um
	 * {@code @PostConstruct}. No Spring Boot 3 esse truque força a criação prematura
	 * do adapter e as alterações se perdem; {@code extendMessageConverters} é o ponto
	 * de extensão correto.
	 *
	 * <p>{@code application/json} permanece na lista porque, com o HAL como default
	 * de JSON, este é o único conversor Jackson registrado — sem ele o
	 * {@code /v3/api-docs} do springdoc responderia 406.
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter messageConverter
					&& converter.getSupportedMediaTypes().contains(MediaTypes.HAL_JSON)) {

				messageConverter.setSupportedMediaTypes(Arrays.asList(
						MediaTypes.HAL_JSON,
						MediaType.APPLICATION_JSON,
						AlgaMediaTypes.V1_APPLICATION_JSON,
						AlgaMediaTypes.V2_APPLICATION_JSON));
			}
		}
	}

	@Bean
	public Filter shallowEtagHeaderFilter() {
		return new ShallowEtagHeaderFilter();
	}

}
