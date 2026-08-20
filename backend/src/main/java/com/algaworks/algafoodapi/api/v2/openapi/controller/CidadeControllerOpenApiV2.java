package com.algaworks.algafoodapi.api.v2.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.algaworks.algafoodapi.api.v2.model.CidadeModelV2;
import com.algaworks.algafoodapi.api.v2.model.input.CidadeInputV2;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Cidades")
public interface CidadeControllerOpenApiV2 {

    @Operation(summary = "Lista as cidades")
    public CollectionModel<CidadeModelV2> getAll();

    @ApiResponses({
    	@ApiResponse(responseCode = "400", description = "ID da cidade inválido"),
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")

    })
    @Operation(summary = "Obtém uma cidade por ID")
    public CidadeModelV2 getById(
    		@Parameter(description = "ID de uma cidade", example = "1") 
    		Long cidadeId);

    @Operation(summary = "Cria uma nova cidade")
    public CidadeModelV2 add(CidadeInputV2 cidade);
    
    @ApiResponses({
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")
    })
    @Operation(summary = "Atualiza uma cidade por ID")
    public ResponseEntity<?> set(
    		@Parameter(description = "ID de uma cidade", example = "1")  
    		Long cidadeId, CidadeInputV2 cidadeInput);

    @ApiResponses({
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")
    })
    @Operation(summary = "Remove uma cidade por ID")
    public void remove(
    	@Parameter(description = "ID de uma cidade", example = "1") 
    	Long cidadeId);
}

