package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;

import com.algaworks.algafoodapi.api.v1.model.ProdutoModel;
import com.algaworks.algafoodapi.api.v1.model.input.ProdutoInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Produtos")
public interface RestauranteProdutoControllerOpenApi {
	

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Parameters({
		@Parameter(description = "Incluir ou não produtos ativos", name = "incluirInativos", in = ParameterIn.QUERY, schema = @Schema(implementation = Boolean.class))
	})
	@Operation(summary = "Listar produtos dos restaurantes")
    public CollectionModel<ProdutoModel> getAll(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		Boolean incluirInativos);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do restaurante ou produto informado inválido"),
		@ApiResponse(responseCode = "404", description = "Restaurante ou produto não encontrado")
	})
	@Operation(summary = "Buscar produto por restaurante")
    public ProdutoModel getById(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID do produto", required = true)
    		Long produtoId);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
	})
	@Operation(summary = "Cria um novo produto para o restaurante")
    public ProdutoModel create(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		ProdutoInput produtoInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Restaurante ou produto não encontrado")
	})
	@Operation(summary = "Atualiza um produto do restaurante")
    public ProdutoModel update(
    		@Parameter(description = "ID do restaurante", required = true)
    		Long restauranteId, 
    		@Parameter(description = "ID do produto", required = true)
    		Long produtoId, ProdutoInput produtoInput);
	
}
