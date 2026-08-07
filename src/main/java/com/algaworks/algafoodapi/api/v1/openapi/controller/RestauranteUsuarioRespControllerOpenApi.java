package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import com.algaworks.algafoodapi.api.v1.model.UsuarioModel;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Restaurantes")
public interface RestauranteUsuarioRespControllerOpenApi {
	
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Lista restaurantes por usuário")
    public CollectionModel<UsuarioModel> getAll(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante ou usuário não encontrado")
	})
	@Operation(summary = "Associa restaurante para um usuário")
    public ResponseEntity<Void> associarResponsavel(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID do usuário", required = true)
    		Long usuarioId);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante ou usuário não encontrado")
	})
	@Operation(summary = "Desassocia um restaurante de um usuário")
    public ResponseEntity<Void> desassociarResponsavel(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID do usuário", required = true)
    		Long usuarioId);
	
}
