package com.algaworks.algafoodapi.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafoodapi.domain.model.Pedido;
import com.algaworks.algafoodapi.domain.repository.PedidoRepository;
import com.algaworks.algafoodapi.domain.service.EnvioEmailService.Mensagem;

@Service
public class FluxoPedidoService {

    @Autowired
    private EmissaoPedidoService emissaoPedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Transactional
    public void confirmar(String codigo) {
    	
    	Pedido pedido = emissaoPedidoService.findOrFail(codigo);
		pedido.confirmar();
		
		pedidoRepository.save(pedido);
		
    }

    @Transactional
    public void cancelar(String codigo) {
    	
       Pedido pedido = emissaoPedidoService.findOrFail(codigo);
       pedido.cancelar();
       
       pedidoRepository.save(pedido);

    }

    @Transactional
    public void entregar(String codigo) {
    	
        Pedido pedido = emissaoPedidoService.findOrFail(codigo);
        pedido.entregar();
        
    }
    
}
