package com.algaworks.algafoodapi.api.v1.convert;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.CidadeController;
import com.algaworks.algafoodapi.api.v1.controller.FormaPagamentoController;
import com.algaworks.algafoodapi.api.v1.controller.PedidoController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteProdutoController;
import com.algaworks.algafoodapi.api.v1.controller.UsuarioController;
import com.algaworks.algafoodapi.api.v1.model.PedidoModel;
import com.algaworks.algafoodapi.core.security.AlgaSecurity;
import com.algaworks.algafoodapi.domain.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PedidoConvert extends RepresentationModelAssemblerSupport<Pedido, PedidoModel> {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AlgaLinks algaLinks;

    @Autowired
    private AlgaSecurity algaSecurity;
    
    public PedidoConvert() {
    	super(PedidoController.class, PedidoModel.class);
    }
    
    public PedidoModel toModel(Pedido pedido) {
    	PedidoModel pedidoModel = createModelWithId(pedido.getId(), pedido);
    	
    	modelMapper.map(pedido, pedidoModel);

        if (algaSecurity.podeGerenciarPedidos(pedido.getCodigo())) {
            if (pedido.podeSerConfirmado()) {
                pedidoModel.add(algaLinks.linkToConfirmacaoPedido(pedidoModel.getCodigo(), "confirmar"));
            }

            if (pedido.podeSerCancelado()) {
                pedidoModel.add(algaLinks.linkToCancelamentoPedido(pedidoModel.getCodigo(), "cancelar"));
            }

            if (pedido.podeSerEntregue()) {
                pedidoModel.add(algaLinks.linkToEntregaPedido(pedidoModel.getCodigo(), "entregar"));
            }
        }
    	pedidoModel.add(algaLinks.linkToPedidos("pedidos"));
    	
    	
    	pedidoModel.getRestaurante().add(
    			algaLinks.linkToRestaurante(pedidoModel.getRestaurante().getId()));
        
        pedidoModel.getCliente().add(
        		algaLinks.linkToCliente(pedidoModel.getCliente().getId()));
        
        // Passamos null no segundo argumento, porque é indiferente para a
        // construção da URL do recurso de forma de pagamento
        pedidoModel.getFormaPagamento().add(
        		algaLinks.linkToFormaPagamento(pedidoModel.getFormaPagamento().getId()));
        
        pedidoModel.getEnderecoEntrega().getCidade().add(
        		algaLinks.linkToEnderecoEntrega(
        				pedidoModel.getEnderecoEntrega().getCidade().getId())
        		);
        
        pedidoModel.getItens().forEach(item -> {
            item.add(
            	algaLinks.linkToItens(pedidoModel.getRestaurante().getId(), item.getProdutoId())
            );
        });
        
        return pedidoModel;
    }

}
