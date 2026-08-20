package com.algaworks.algafoodapi.api.v2.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v2.model.CidadeModelV2;

import lombok.Data;

@Schema(name = "CidadesModel")
@Data
public class CidadesModelOpenApiV2 {
	
	private CidadeEmbeddedModelOpenApi _embedded;
	private Links _links;
	
	@Schema(name = "CidadesEmbeddedModel")
	@Data
	public class CidadeEmbeddedModelOpenApi {
		
		private List<CidadeModelV2> cidades;
	}
	
}
