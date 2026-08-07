package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.algafoodapi.api.v1.model.FotoProdutoModel;
import com.algaworks.algafoodapi.api.v1.model.input.FotoProdutoInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Produtos")
public interface RestauranteProdFotoControllerOpenApi {

	@Operation(summary = "Atualiza a foto do produto de um restaurante")
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Produto de restaurante não encontrado")
	})
	FotoProdutoModel atualizarFoto(
			@Parameter(description = "ID do restaurante", example = "1", required = true)
			Long restauranteId,
			
			@Parameter(description = "ID do produto", example = "1", required = true)
			Long produtoId,
			
			FotoProdutoInput fotoProdutoInput,
			
			@Parameter(description = "Arquivo da foto do produto (máximo 500KB, apenas JPG e PNG)", required = true)
			MultipartFile arquivo) throws IOException;

	@Operation(summary = "Exclui a foto do produto de um restaurante")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do restaurante ou produto inválido"),
		@ApiResponse(responseCode = "404", description = "Foto de produto não encontrada")
	})
	void excluir(
			@Parameter(description = "ID do restaurante", example = "1", required = true)
			Long restauranteId,
			
			@Parameter(description = "ID do produto", example = "1", required = true)
			Long produtoId);

	@Operation(summary = "Busca a foto do produto de um restaurante")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID do restaurante ou produto inválido"),
		@ApiResponse(responseCode = "404", description = "Foto de produto não encontrada")
	})
	FotoProdutoModel buscar(
			@Parameter(description = "ID do restaurante", example = "1", required = true)
			Long restauranteId,
			
			@Parameter(description = "ID do produto", example = "1", required = true)
			Long produtoId);

	@Operation(summary = "Busca a foto do produto de um restaurante", hidden = true)
	ResponseEntity<?> servir(Long restauranteId, Long produtoId, 
			String acceptHeader) 
			throws HttpMediaTypeNotAcceptableException;
	
}
