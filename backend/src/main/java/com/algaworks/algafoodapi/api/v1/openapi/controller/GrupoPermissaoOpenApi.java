package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;

import com.algaworks.algafoodapi.api.v1.model.PermissaoModel;

@Tag(name = "Grupos")
public interface GrupoPermissaoOpenApi {

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do grupo inválido"),
		@ApiResponse(responseCode = "404", description = "Grupo não encontrado")
	})
	@Operation(summary = "Lista as permissões do grupo")
    public CollectionModel<PermissaoModel> getAll(
    		@Parameter(description = "ID do grupo")
    		Long grupoId);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Grupo ou permissão não encontrado")
	})
	@Operation(summary = "Desassocia um grupo de uma permissão")
    public void desassociar(
    		@Parameter(description = "ID do grupo")
    		Long grupoId,
    		@Parameter(description = "ID da permissão")
    		Long permissaoId);
  
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Grupo ou permissão não encontrado")
	})
	@Operation(summary = "Associa um grupo para uma permissão")
    public void associar(
    		@Parameter(description = "ID do grupo")
    		Long grupoId,
    		@Parameter(description = "ID do permissão")
    		Long permissaoId);
    
}
