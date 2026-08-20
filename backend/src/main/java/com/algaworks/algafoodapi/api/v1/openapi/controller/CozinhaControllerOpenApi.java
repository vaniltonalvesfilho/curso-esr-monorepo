package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;

import com.algaworks.algafoodapi.api.v1.model.CozinhaModel;
import com.algaworks.algafoodapi.api.v1.model.input.CozinhaInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Cozinhas")
public interface CozinhaControllerOpenApi {
	
	@Operation(summary = "Lista as cozinhas")
    public PagedModel<CozinhaModel> getAll(Pageable pageable);

	@Operation(summary = "Obtém cozinha por ID")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID da cozinha inválido"),
		@ApiResponse(responseCode = "404", description = "Cozinha não encontrada")
	})
    public CozinhaModel getById(
    		@Parameter(description = "ID de uma cozinha", example = "1")
    		Long cozinhaId);

	@Operation(summary = "Adiciona uma nova cozinha")
    public CozinhaModel add(CozinhaInput cozinhaInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Cozinha não encontrada")
	})
	@Operation(summary = "Atualiza uma cozinha por ID")
    public CozinhaModel set(
    		@Parameter(description = "ID de uma cozinha", example = "1")
    		Long cozinhaId, 
    		CozinhaInput cozinhaInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Cozinha não encontrada")
	})
	@Operation(summary = "Remove uma cozinha por ID")
    public void remove(
    		@Parameter(description = "ID de uma cozinha", example = "1")
    		Long cozinhaId);
	
}
