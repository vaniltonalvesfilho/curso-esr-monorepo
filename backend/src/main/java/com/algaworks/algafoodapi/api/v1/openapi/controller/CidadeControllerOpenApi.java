package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.algaworks.algafoodapi.api.v1.model.CidadeModel;
import com.algaworks.algafoodapi.api.v1.model.CidadeResumoModel;
import com.algaworks.algafoodapi.api.v1.model.input.CidadeInput;
import com.algaworks.algafoodapi.domain.exception.EstadoNaoEncontradoException;
import com.algaworks.algafoodapi.domain.exception.NegocioException;
import com.algaworks.algafoodapi.domain.model.Cidade;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Cidades")
public interface CidadeControllerOpenApi {

    @Operation(summary = "Lista as cidades")
    public CollectionModel<CidadeModel> getAll();

    @ApiResponses({
    	@ApiResponse(responseCode = "400", description = "ID da cidade inválido"),
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")

    })
    @Operation(summary = "Obtém uma cidade por ID")
    public CidadeModel getById(
    		@Parameter(description = "ID de uma cidade", example = "1") 
    		Long cidadeId);

    @Operation(summary = "Cria uma nova cidade")
    public CidadeModel add(CidadeInput cidade);
    
    @ApiResponses({
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")
    })
    @Operation(summary = "Atualiza uma cidade por ID")
    public ResponseEntity<?> set(
    		@Parameter(description = "ID de uma cidade", example = "1")  
    		Long cidadeId, CidadeInput cidadeInput);

    @ApiResponses({
    	@ApiResponse(responseCode = "404", description = "Cidade não econtrada")
    })
    @Operation(summary = "Remove uma cidade por ID")
    public void remove(
    	@Parameter(description = "ID de uma cidade", example = "1") 
    	Long cidadeId);
}
