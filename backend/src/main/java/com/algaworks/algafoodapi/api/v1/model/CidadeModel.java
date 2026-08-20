package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "cidades")
@Setter
@Getter
public class CidadeModel extends RepresentationModel<CidadeModel> {

	@Schema(example = "1")
    private Long id;
	
	@Schema(example = "Uberlândia", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nome;
	
    private EstadoModel estado;
    
}
