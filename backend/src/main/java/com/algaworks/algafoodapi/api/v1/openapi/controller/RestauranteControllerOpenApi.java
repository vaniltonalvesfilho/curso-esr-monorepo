package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import com.algaworks.algafoodapi.api.v1.model.RestauranteApenasNomeModel;
import com.algaworks.algafoodapi.api.v1.model.RestauranteBasicModel;
import com.algaworks.algafoodapi.api.v1.model.RestauranteModel;
import com.algaworks.algafoodapi.api.v1.model.input.RestauranteInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Restaurantes")
public interface RestauranteControllerOpenApi {
	
    public CollectionModel<RestauranteBasicModel> listar();

   
	@Operation(summary = "Lista restaurantes", hidden = true)
    public CollectionModel<RestauranteApenasNomeModel> listarApenasNome();

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Busca um restaurante por ID")
    public RestauranteModel buscar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);

	@Operation(summary = "Cria um novo restaurante")
    public RestauranteModel adicionar(RestauranteInput restauranteInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Atualiza um restaurante por ID")
    public RestauranteModel atualizar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, RestauranteInput restauranteInput);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Remove um restaurante")
    public void remover(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Ativa um restaurante por ID")
    public ResponseEntity<Void> ativar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);
 
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Desativa um restaurante por ID")
    public ResponseEntity<Void> inativar(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Abre um retaurante por ID")
    public ResponseEntity<Void> abertura(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);
    
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Fecha um restaurante por ID")
    public ResponseEntity<Void> fechamento(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Ativa múltiplos restaurantes por ID")
    public void ativarMultiplos(
    		@Parameter(example = "[2, 3, 7]")
    		List<Long> restauranteIds);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Inativa múltiplos restaurantes por ID")
    public void inativarMultiplos(
    		@Parameter(example = "[2, 3, 7]")
    		List<Long> restauranteIds);

}
