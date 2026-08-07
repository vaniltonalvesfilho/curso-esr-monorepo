package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class ItemPedidoModel extends RepresentationModel<ItemPedidoModel> {

	@Schema(description = "ID do produto associado ao item", example = "1")
    private Long produtoId;
	
	@Schema(example = "Prime Rib")
    private String produtoNome;
	
	@Schema(example =  "3")
    private Integer quantidade;
	
	@Schema(description = "Preço do item", example = "23.34")
    private BigDecimal precoUnitario;
	
	@Schema(description = "Preço do item multiplicado pela quantidade", example = "70.02")
    private BigDecimal precoTotal;
	
	@Schema(description = "Alguma observação sobre o preparo do pedido, entrega e etc.", example = "Bem acebolado por favor")
    private String observacao;

}
