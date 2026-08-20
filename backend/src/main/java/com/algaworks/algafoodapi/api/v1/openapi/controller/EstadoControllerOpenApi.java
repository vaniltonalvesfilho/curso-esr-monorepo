package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestBody;

import com.algaworks.algafoodapi.api.v1.model.EstadoModel;
import com.algaworks.algafoodapi.api.v1.model.input.EstadoInput;
import com.algaworks.algafoodapi.domain.model.Estado;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Estados")
public interface EstadoControllerOpenApi {
	
	@Operation(summary = "Lista os estados")
    public CollectionModel<EstadoModel> getAll();
    
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Estado não encontrado"),
		@ApiResponse(responseCode = "400", description = "ID informado inválido")
	})
	@Operation(summary = "Busca um estado por ID")
    public EstadoModel getById(
    		@Parameter(description = "ID do estado", required = true)
    		Long estadoId);

	@Operation(summary = "Cria um novo estado")
    public EstadoModel add(EstadoInput estadoInput);
    
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Estado não encontrado")
	})
	@Operation(summary = "Atualiza um estado por ID")
    public EstadoModel set(
    		@Parameter(description = "ID do estado", required = true)
    		Long estadoId, EstadoInput estadoInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Estado não encontrado")
	})
	@Operation(summary = "Remove um estado por ID")
    public void remove(
    		@Parameter(description = "ID do estado", required = true)
    		Long estadoId);
    
}
