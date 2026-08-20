package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v1.model.CozinhaModel;

import lombok.Data;

@Schema(name = "CozinhasModel")
@Data
public class CozinhasModelOpenApi {

	private CozinhaEmbeddedModelOpenApi _embedded;
	private Links _links;
	private PageModelOpenApi page;
	
	@Schema(name = "CozinhasEmbeddedModel")
	@Data
	private class CozinhaEmbeddedModelOpenApi {
		
		private List<CozinhaModel> cozinhas;
	}
	
}
