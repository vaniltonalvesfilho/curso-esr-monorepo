package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Pedidos")
public interface FluxoPedidoControllerOpenApi {

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	})
	@Operation(summary = "Confirma um pedido pelo código")
    public ResponseEntity<Void> confirmar(
    		@Parameter(description = "Código do pedido", required = true)
    		String codigo);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	})
	@Operation(summary = "Cancela um pedido pelo código")
    public ResponseEntity<Void> cancelar(
    		@Parameter(description = "Código do pedido", required = true)
    		String codigo);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	})
	@Operation(summary = "Registra a entrega do pedido pelo código")
    public ResponseEntity<Void> entregar(
    		@Parameter(description = "Código do pedido", required = true)
    		String codigo);
	
}
