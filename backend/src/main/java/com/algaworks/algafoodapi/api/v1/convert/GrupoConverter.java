package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.GrupoController;
import com.algaworks.algafoodapi.api.v1.model.GrupoModel;
import com.algaworks.algafoodapi.domain.model.Grupo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GrupoConverter extends RepresentationModelAssemblerSupport<Grupo, GrupoModel> {

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AlgaLinks algaLinks;
    
    public GrupoConverter() {
    	super(GrupoController.class, GrupoModel.class);
    }

    public GrupoModel toModel(Grupo grupo) {
    	var grupoModel = createModelWithId(grupo.getId(), grupo);
        
    	modelMapper.map(grupo, grupoModel);
        grupoModel.add(algaLinks.linkToGrupos("grupos"));
    	grupoModel.add(algaLinks.linkToGrupoPermissao(grupo.getId(), "permissoes"));
    	
        return grupoModel;
    }

    @Override
    public CollectionModel<GrupoModel> toCollectionModel(Iterable<? extends Grupo> entities) {
    	return super.toCollectionModel(entities)
    			.add(algaLinks.linkToGrupos());
    }
}
