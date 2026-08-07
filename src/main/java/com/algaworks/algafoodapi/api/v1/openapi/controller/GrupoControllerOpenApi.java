package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;

import com.algaworks.algafoodapi.api.v1.model.GrupoModel;
import com.algaworks.algafoodapi.api.v1.model.input.GrupoInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Grupos")
public interface GrupoControllerOpenApi {
	
	@Operation(summary = "Lista os grupos")
    public CollectionModel<GrupoModel> getAll();

	@Operation(summary = "Obtém um grupo por ID")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do grupo inválido"),
		@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
	})
    public GrupoModel getById(Long grupoId);

	@Operation(summary = "Cria um novo grupo")
    public GrupoModel create(GrupoInput grupoInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
	})
	@Operation(summary = "Atualiza um grupo por ID")
    public GrupoModel update(Long grupoId, GrupoInput grupoInput);
   
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
	})
	@Operation(summary = "Remove uma cidade por ID")
    public void delete(Long grupoId);
	
}
