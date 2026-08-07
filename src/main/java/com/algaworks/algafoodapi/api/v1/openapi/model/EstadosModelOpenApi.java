package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v1.model.EstadoModel;

import lombok.Data;

@Schema(name = "EstadosModel")
@Data
public class EstadosModelOpenApi {
	
	private EstadoEmbeddedModelOpenApi _embedded;
	private Links _links;
	private PageModelOpenApi page;

	@Schema(name = "EstadosEmbeddedModelOpenApi")
	@Data
	public class EstadoEmbeddedModelOpenApi {
		
		private List<EstadoModel> estados;
		
	}
	
}
