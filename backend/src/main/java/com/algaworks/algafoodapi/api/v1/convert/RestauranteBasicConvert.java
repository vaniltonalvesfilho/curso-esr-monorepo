package com.algaworks.algafoodapi.api.v1.convert;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteController;
import com.algaworks.algafoodapi.api.v1.model.RestauranteBasicModel;
import com.algaworks.algafoodapi.domain.model.Restaurante;

@Component
public class RestauranteBasicConvert extends 
	RepresentationModelAssemblerSupport<Restaurante, RestauranteBasicModel> {
	
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AlgaLinks algaLinks;
	
	public RestauranteBasicConvert() {
		super(RestauranteController.class, RestauranteBasicModel.class);
	}
	
	@Override
	public RestauranteBasicModel toModel(Restaurante restaurante) {
		var restauranteModel = createModelWithId(restaurante.getId(), restaurante);
		
		modelMapper.map(restaurante, restauranteModel);
		
		restauranteModel.add(algaLinks.linkToRestaurantes("restaurantes"));
		restauranteModel.getCozinha().add(algaLinks.linkToCozinha(restaurante.getCozinha().getId()));
		
		return restauranteModel;
	}
	
	@Override
	public CollectionModel<RestauranteBasicModel> toCollectionModel(Iterable<? extends Restaurante> entities) {
		return super.toCollectionModel(entities)
					.add(algaLinks.linkToRestaurantes());
	}
}
