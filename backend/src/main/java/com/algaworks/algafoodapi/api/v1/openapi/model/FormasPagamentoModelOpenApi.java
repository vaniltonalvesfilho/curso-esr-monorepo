package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v1.model.FormaPagamentoModel;

import lombok.Data;

@Schema(name = "FormasPagamentoModel")
@Data
public class FormasPagamentoModelOpenApi {
	
	private FormaPagamentoEmbeddedModelOpenApi _embedded;
	private Links _links;
	
	@Schema(name = "FormasPagamentoEmbeddedModel")
	@Data
	public class FormaPagamentoEmbeddedModelOpenApi {
		
		private List<FormaPagamentoModel> formasPagamento;
		
	}
	
}
