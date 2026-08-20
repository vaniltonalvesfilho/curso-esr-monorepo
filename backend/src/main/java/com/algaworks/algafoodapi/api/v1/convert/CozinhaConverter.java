package com.algaworks.algafoodapi.api.v1.convert;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.CozinhaController;
import com.algaworks.algafoodapi.api.v1.model.CozinhaModel;
import com.algaworks.algafoodapi.domain.model.Cozinha;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CozinhaConverter extends 
	RepresentationModelAssemblerSupport<Cozinha, CozinhaModel>{

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AlgaLinks algaLinks;

    public CozinhaConverter() {
    	super(CozinhaController.class, CozinhaModel.class);
    }
    
    @Override
    public CozinhaModel toModel(Cozinha cozinha) {
    	CozinhaModel cozinhaModel = createModelWithId(cozinha.getId(), cozinha);
        modelMapper.map(cozinha, cozinhaModel);
        
        cozinhaModel.add(
        		algaLinks.linkToCozinhas()
        );
        
        return cozinhaModel;
    }

   


}
