package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import com.algaworks.algafoodapi.api.v1.model.FormaPagamentoModel;
import com.algaworks.algafoodapi.api.v1.model.input.FormaPagamentoInput;
import com.algaworks.algafoodapi.api.v1.openapi.model.FormasPagamentoModelOpenApi;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Formas Pagamento")
public interface FormaPagamentoControllerOpenApi {
	
	
	@Operation(summary = "Lista as formas de pagamento")
    public ResponseEntity<CollectionModel<FormaPagamentoModel>> getAll(ServletWebRequest request);
    
	@Operation(summary = "Obtém forma de pagamento por ID")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "ID informado inválido"),
		@ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada")
		
	})
    public ResponseEntity<FormaPagamentoModel> getById(
    		@Parameter(example = "1", description = "ID de uma forma de pagamento")
    		Long formaPagamentoId, 
    		ServletWebRequest request);

	@Operation(summary = "Adiciona uma nova forma de pagamento")
    public FormaPagamentoModel create(FormaPagamentoInput formaPagamentoInput);

	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada")		
	})
	@Operation(summary = "Atualiza uma forma de pagamento por ID")
    public FormaPagamentoModel update(
    		@Parameter(example = "1", description = "ID de uma forma de pagamento")
    		Long formaPagamentoId, 
    		FormaPagamentoInput formaPagamentoInput);
    
	@ApiResponses({
		@ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada")
	})
	@Operation(summary = "Remove uma forma de pagamento por ID")
    public void delete(
    		@Parameter(example = "1", description = "ID de uma forma de pagamento")
    		Long formaPagamentoId);
	
}
