package com.algaworks.algafoodapi.api.v2.assembler;

import com.algaworks.algafoodapi.api.v2.AlgaLinksV2;
import com.algaworks.algafoodapi.api.v2.controller.CidadeControllerV2;
import com.algaworks.algafoodapi.api.v2.model.CidadeModelV2;
import com.algaworks.algafoodapi.domain.model.Cidade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CidadeConverterV2 extends 
	RepresentationModelAssemblerSupport<Cidade, CidadeModelV2> {
	

	@Autowired
    private ModelMapper mapper;
	
	@Autowired
	private AlgaLinksV2 algaLinks;
	
    public CidadeConverterV2() {
		super(CidadeControllerV2.class, CidadeModelV2.class);
	}

    @Override
    public CidadeModelV2 toModel(Cidade cidade) {
        CidadeModelV2 cidadeModel = createModelWithId(cidade.getId(), cidade);
        
        mapper.map(cidade, cidadeModel);
        
        cidadeModel.add(algaLinks.linkToCidades());
               
        return cidadeModel;
    }

    @Override
    public CollectionModel<CidadeModelV2> toCollectionModel(Iterable<? extends Cidade> entities) {
    	return super.toCollectionModel(entities)
    				.add(algaLinks.linkToCidades());
    }
    
}
