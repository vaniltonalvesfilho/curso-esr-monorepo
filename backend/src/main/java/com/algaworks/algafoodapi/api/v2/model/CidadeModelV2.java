package com.algaworks.algafoodapi.api.v2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Getter;
import lombok.Setter;

@Schema(name = "CidadeModel")
@Relation(collectionRelation = "cidades")
@Setter
@Getter
public class CidadeModelV2 extends RepresentationModel<CidadeModelV2> {

	@Schema(example = "1")
    private Long idCidade;
	
	@Schema(example = "Uberlândia", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nomeCidade;
	
	@Schema(example = "1")
    private Long idEstado;
	
	@Schema(example = "Minas Gerais", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nomeEstado;
    
}
