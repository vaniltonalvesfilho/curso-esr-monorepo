package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteController;
import com.algaworks.algafoodapi.api.v1.model.RestauranteModel;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RestauranteConverter extends RepresentationModelAssemblerSupport<Restaurante, RestauranteModel> {

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AlgaLinks algaLinks;
    
    public RestauranteConverter() {
    	super(RestauranteController.class, RestauranteModel.class);
	}

    @Override
    public RestauranteModel toModel(Restaurante restaurante) {
        var restauranteModel = createModelWithId(restaurante.getId(), restaurante);
        
        modelMapper.map(restaurante, restauranteModel);
        
        restauranteModel.add(algaLinks.linkToRestaurantes("restaurantes"));
        
        if (restaurante.ativacaoPermitida()) {
        	restauranteModel.add(algaLinks.linkToAtivarRestaurante(restaurante.getId(), "ativar"));
        } 
        
        if (restaurante.inativacaoPermitida()){
        	restauranteModel.add(algaLinks.linkToInativarRestaurante(restaurante.getId(), "inativar"));
        }
        
        if (restaurante.aberturaPermitida()) {
        	restauranteModel.add(algaLinks.linkToAbrirRestaurante(restaurante.getId(), "abrir"));
        }
        
        if (restaurante.fechamentoPermitido()){
        	restauranteModel.add(algaLinks.linkToFecharRestaurante(restaurante.getId(), "fechar"));
        }
        
        restauranteModel.getCozinha().add(
                algaLinks.linkToCozinha(restaurante.getCozinha().getId()));
        
        // Tem restaurante que não tem endereço
        if (restauranteModel.getEndereco() != null && restauranteModel.getEndereco().getCidade() != null) {
        	restauranteModel.getEndereco().getCidade().add(
                algaLinks.linkToCidade(restaurante.getEndereco().getCidade().getId()));
        }
        
        restauranteModel.add(algaLinks.linkToRestauranteFormasPagamento(restaurante.getId(), 
                "formas-pagamento"));
        
        restauranteModel.add(algaLinks.linkToResponsaveisRestaurantes(restaurante.getId(), 
                "responsaveis"));
        
        restauranteModel.add(algaLinks.linkToProdutos(restaurante.getId(), "produtos"));
       
        
        
        return restauranteModel;
    }

    @Override
    public CollectionModel<RestauranteModel> toCollectionModel(
    		Iterable<? extends Restaurante> entities) {
    	return super.toCollectionModel(entities)
    			.add(algaLinks.linkToRestaurantes());
    }
}
