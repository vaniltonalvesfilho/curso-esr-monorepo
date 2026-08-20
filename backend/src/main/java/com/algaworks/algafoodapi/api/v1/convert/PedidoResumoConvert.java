package com.algaworks.algafoodapi.api.v1.convert;


import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.PedidoController;
import com.algaworks.algafoodapi.api.v1.model.PedidoResumoModel;
import com.algaworks.algafoodapi.domain.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;


@Component
public class PedidoResumoConvert extends 
	RepresentationModelAssemblerSupport<Pedido, PedidoResumoModel> {
	
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AlgaLinks algaLinks;
    
    public PedidoResumoConvert() {
    	super(PedidoController.class, PedidoResumoModel.class);
    }
    
    public PedidoResumoModel toModel(Pedido pedido) {
    	 PedidoResumoModel pedidoModel = createModelWithId(pedido.getCodigo(), pedido);
         modelMapper.map(pedido, pedidoModel);

        pedidoModel.add(algaLinks.linkToPedidos("pedidos"));
        
        if (pedido.podeSerConfirmado()) {
        	pedidoModel.add(algaLinks.linkToConfirmacaoPedido(pedidoModel.getCodigo(), "confirmar"));
        }
        
        if (pedido.podeSerCancelado()) {
        	pedidoModel.add(algaLinks.linkToCancelamentoPedido(pedidoModel.getCodigo(), "cancelar"));
        }
        
        if (pedido.podeSerEntregue()) {
        	pedidoModel.add(algaLinks.linkToEntregaPedido(pedidoModel.getCodigo(), "entrega"));
        }

                  
        pedidoModel.getRestaurante().add(
        		algaLinks.linkToRestaurante(pedidoModel.getRestaurante().getId()));
     
        pedidoModel.getCliente().add(
        		algaLinks.linkToCliente(pedidoModel.getCliente().getId()));
     
        return pedidoModel;
    }

}
