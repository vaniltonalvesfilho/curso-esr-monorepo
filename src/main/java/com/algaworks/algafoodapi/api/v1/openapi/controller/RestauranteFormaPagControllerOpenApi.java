package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import com.algaworks.algafoodapi.api.v1.model.FormaPagamentoModel;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Restaurantes")
public interface RestauranteFormaPagControllerOpenApi {
	
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Lista formas de pagamento pelo ID do restaurante")
    public CollectionModel<FormaPagamentoModel> listar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);
	
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante ou forma de pagamento não encontrado")
	})
	@Operation(summary = "Desassociar uma forma de pagamento ao restaurante")
    public ResponseEntity<Void> desassociar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID da forma de pagamento", required = true)
    		Long formaPagamentoId);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante ou forma de pagamento não encontrado")
	})
	@Operation(summary = "Associa uma forma de pagamento ao restaurante")
    public ResponseEntity<Void> associar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID da forma de pagamento", required = true)
    		Long formaPagamentoId);
	
}
