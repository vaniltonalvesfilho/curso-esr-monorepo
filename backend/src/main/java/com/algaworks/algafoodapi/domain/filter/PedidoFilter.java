package com.algaworks.algafoodapi.domain.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Setter
@Getter
public class PedidoFilter {

	@Schema(description = "ID do cliente (usuário)", example = "1")
    private Long clienteId;
	
	@Schema(description = "ID do restaurante", example = "1")
    private Long restauranteId;

	@Schema(description = "Data de criação do pedido", example = "2021-06-20T19:48:59.167216Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dataCriacaoInicio;

	@Schema(description = "Data da última atualização do pedido", example = "2021-06-20T19:48:59.167216Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dataCriacaoFim;

}
