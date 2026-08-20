package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.CidadeController;
import com.algaworks.algafoodapi.api.v1.model.CidadeModel;
import com.algaworks.algafoodapi.domain.model.Cidade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CidadeConverter extends 
	RepresentationModelAssemblerSupport<Cidade, CidadeModel> {
	

	@Autowired
    private ModelMapper mapper;
	
	@Autowired
	private AlgaLinks algaLinks;
	
    public CidadeConverter() {
		super(CidadeController.class, CidadeModel.class);
	}

    @Override
    public CidadeModel toModel(Cidade cidade) {
        CidadeModel cidadeModel = createModelWithId(cidade.getId(), cidade);
        
        mapper.map(cidade, cidadeModel);
        
        cidadeModel.add(algaLinks.linkToCidades());
       
        cidadeModel.getEstado().add(algaLinks.linkToEstados());
        
        return cidadeModel;
    }

    @Override
    public CollectionModel<CidadeModel> toCollectionModel(Iterable<? extends Cidade> entities) {
    	return super.toCollectionModel(entities)
    				.add(algaLinks.linkToCidades());
    }
    
}
