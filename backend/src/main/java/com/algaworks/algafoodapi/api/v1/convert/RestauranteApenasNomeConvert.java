package com.algaworks.algafoodapi.api.v1.convert;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteController;
import com.algaworks.algafoodapi.api.v1.model.RestauranteApenasNomeModel;
import com.algaworks.algafoodapi.domain.model.Restaurante;

@Component
public class RestauranteApenasNomeConvert extends 
	RepresentationModelAssemblerSupport<Restaurante, RestauranteApenasNomeModel> {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AlgaLinks algaLinks;
	
	public RestauranteApenasNomeConvert() {
		super(RestauranteController.class, RestauranteApenasNomeModel.class);
	}
	
	@Override
	public RestauranteApenasNomeModel toModel(Restaurante restaurante) {
		var restauranteModel = createModelWithId(restaurante.getId(), restaurante);
		
		modelMapper.map(restaurante, restauranteModel);
		restauranteModel.add(algaLinks.linkToRestaurantes("restaurantes"));
		
		
		return restauranteModel;
	}
	
	@Override
	public CollectionModel<RestauranteApenasNomeModel> toCollectionModel(Iterable<? extends Restaurante> entities) {
		return super.toCollectionModel(entities)
				.add(algaLinks.linkToRestaurantes());
	}
	
}
