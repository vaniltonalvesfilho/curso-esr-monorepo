package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;

import com.algaworks.algafoodapi.api.v1.model.PedidoModel;
import com.algaworks.algafoodapi.api.v1.model.PedidoResumoModel;
import com.algaworks.algafoodapi.api.v1.model.input.PedidoInput;
import com.algaworks.algafoodapi.domain.filter.PedidoFilter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Pedidos")
public interface PedidoControllerOpenApi {
	
	 	@Parameters({
	 		@Parameter(description = "Nomes das propriedades para filtrar na resposta, separados por vírgula", name = "campos", in = ParameterIn.QUERY)
	 	})
	 	@Operation(summary = "Busca os pedidos")
		public PagedModel<PedidoResumoModel> pesquisar(
				PedidoFilter filter, 
				@PageableDefault(size = 10) Pageable pageable);

	    @Parameters({
	    	@Parameter(description = "Nomes das propriedades para filtrar na resposta, separados por vírgula", name = "campos", in = ParameterIn.QUERY)
	    })
	    @ApiResponses({
	    	@ApiResponse(responseCode = "400", description = "Código inválido"),
	    	@ApiResponse(responseCode = "404", description = "Pedido com código informado não encontrado")
	 	})
	    @Operation(summary = "Busca um pedido pelo código")
		public PedidoModel getById(
				@Parameter(example = "f9981ca4-5a5e-4da3-af04-933861df3e55", description = "Código do pedido")
				String codigo);
	    
	    @Operation(summary = "Emite um novo pedido")
		public PedidoModel add(PedidoInput pedidoInput);
	
}
