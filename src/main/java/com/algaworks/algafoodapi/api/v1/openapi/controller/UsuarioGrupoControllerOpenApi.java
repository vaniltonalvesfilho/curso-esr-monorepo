package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import com.algaworks.algafoodapi.api.v1.model.GrupoModel;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Usuários")
public interface UsuarioGrupoControllerOpenApi {

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do usuário inválido"),
		@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	})
	@Operation(summary = "Lista todos os grupos do usuário por ID")
	public CollectionModel<GrupoModel> getAll(
			@Parameter(description = "ID do usuário", example = "1", required = true)
			Long usuarioId);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do usuário ou grupo inválido"),
		@ApiResponse(responseCode = "404", description = "Usuário ou grupo não encontrado")
	})
	@Operation(summary = "Associa um usuário para um grupo")
    public ResponseEntity<Void> associar(
    		@Parameter(description = "ID do usuário", example = "1", required = true)
    		Long usuarioId,
    		@Parameter(description = "ID do grupo", example = "1", required = true)
    		Long grupoId);

	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do usuário ou grupo inválido"),
		@ApiResponse(responseCode = "404", description = "Usuário ou grupo não encontrado")
	})
	@Operation(summary = "Dessasocia um usuário de um grupo")
    public ResponseEntity<Void> desassociar(
    		@Parameter(description = "ID do usuário", example = "1", required = true)
    		Long usuarioId,
    		@Parameter(description = "ID do grupo", example = "1", required = true)
    		Long grupoId);
}
