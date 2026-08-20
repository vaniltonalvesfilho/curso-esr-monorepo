package com.algaworks.algafoodapi.api.v1.convert;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.EstadoController;
import com.algaworks.algafoodapi.api.v1.model.EstadoModel;
import com.algaworks.algafoodapi.domain.model.Estado;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class EstadoConverter extends RepresentationModelAssemblerSupport<Estado, EstadoModel> {
	
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AlgaLinks algaLinks;
    
    public EstadoConverter() {
    	super(EstadoController.class, EstadoModel.class);
    }
    
    public EstadoModel toModel(Estado estado) {
    	EstadoModel estadoModel = createModelWithId(estado.getId(), estado);
    	modelMapper.map(estado, estadoModel);
        return estadoModel;
    }
    
    @Override
    public CollectionModel<EstadoModel> toCollectionModel(Iterable<? extends Estado> entities) {
    	return super.toCollectionModel(entities)
    			.add(algaLinks.linkToEstados());
    }

}
