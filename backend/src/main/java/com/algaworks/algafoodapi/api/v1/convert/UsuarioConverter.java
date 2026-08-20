package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.controller.UsuarioController;
import com.algaworks.algafoodapi.api.v1.controller.UsuarioGrupoController;
import com.algaworks.algafoodapi.api.v1.model.UsuarioModel;
import com.algaworks.algafoodapi.domain.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioConverter extends 
	RepresentationModelAssemblerSupport<Usuario, UsuarioModel> {
	
    @Autowired
    private ModelMapper modelMapper;
	
    @Autowired
    private AlgaLinks algaLinks;
    
	public UsuarioConverter() {
		super(UsuarioController.class, UsuarioModel.class);
	}
	


    public UsuarioModel toModel(Usuario usuario) {
    	UsuarioModel usuarioModel = createModelWithId(usuario.getId(), usuario);
    	
    	modelMapper.map(usuario, usuarioModel);
    	
    	usuarioModel.add(
    		algaLinks.linkToUsuarios("usuarios")
    	);
    	
    	usuarioModel.add(
    		algaLinks.linkToGrupoUsuarios(usuarioModel.getId(), "grupos")
    	);
    	    	
    	
        return usuarioModel;
    }

    @Override
    public CollectionModel<UsuarioModel> toCollectionModel(Iterable<? extends Usuario> entities) {
    	return super.toCollectionModel(entities)
    			.add(algaLinks.linkToUsuarios());
    }
}
