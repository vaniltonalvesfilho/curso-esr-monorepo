package com.algaworks.algafoodapi.domain.event;

import com.algaworks.algafoodapi.domain.model.Pedido;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidoConfirmadoEvent {
	
	private Pedido pedido;
	
}
