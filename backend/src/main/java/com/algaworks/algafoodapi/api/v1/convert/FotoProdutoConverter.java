package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteProdutoFotoController;
import com.algaworks.algafoodapi.api.v1.model.FotoProdutoModel;
import com.algaworks.algafoodapi.domain.model.FotoProduto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class FotoProdutoConverter extends RepresentationModelAssemblerSupport<FotoProduto, FotoProdutoModel> {

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AlgaLinks algaLinks;
    
    public FotoProdutoConverter() {
    	super(RestauranteProdutoFotoController.class, FotoProdutoModel.class);
	}
    
    @Override
    public FotoProdutoModel toModel(FotoProduto fotoProduto) {
        var fotoProdutoModel = modelMapper.map(fotoProduto, FotoProdutoModel.class);
        
        var restauranteId = fotoProduto.getRestauranteId();
        var produtoId = fotoProduto.getProduto().getId();
        
        fotoProdutoModel.add(algaLinks.linkToFotoProduto(
        		restauranteId, produtoId));
        fotoProdutoModel.add(algaLinks.linkToProduto(restauranteId, produtoId, "produto"));
    	return fotoProdutoModel;
    	
    }
}
