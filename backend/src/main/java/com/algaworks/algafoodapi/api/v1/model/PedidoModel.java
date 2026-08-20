package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class PedidoModel extends RepresentationModel<PedidoModel> {

	@Schema(example = "f9981ca4-5a5e-4da3-af04-933861df3e55")
    private String codigo;
	
	@Schema(description = "Status em que o pedido se encontra", example = "CRIADO")
    private String status;

	@Schema(description = "Valor sem a taxa de frete incluída", example = "12.00")
    private BigDecimal subtotal;
	
	@Schema(description = "Taxa de frete para entrega", example = "3.00")
    private BigDecimal taxaFrete;
	
	@Schema(description = "Valor do subtotal mais taxa de frete", example = "15.00")
    private BigDecimal valorTotal;

	
	
	@Schema(description = "Data/hora quando o pedido foi criado", example = "2021-06-22T03:35:41Z")
    private OffsetDateTime dataCriacao;
	
	@Schema(description = "Data/hora quando o pedido foi confirmado pelo restaurante", example = "2021-06-22T03:42:41Z")
    private OffsetDateTime dataConfirmacao;
	
	@Schema(description = "Data/hora de cancelamento do pedido pelo usuário", example = "2021-06-22T03:54:41Z")
    private OffsetDateTime dataCancelamento;
	
	@Schema(description = "Data/hora de entrega do pedido", example = "2021-06-22T04:35:41Z")
    private OffsetDateTime dataEntrega;

	private RestauranteBasicModel restaurante;
	private EnderecoModel enderecoEntrega;
    private UsuarioModel cliente;
    private FormaPagamentoModel formaPagamento;
    
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoModel> itens;
}
