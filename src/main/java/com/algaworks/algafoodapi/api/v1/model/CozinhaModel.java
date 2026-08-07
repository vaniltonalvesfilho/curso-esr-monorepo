package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "cozinhas")
@Setter
@Getter
public class CozinhaModel extends RepresentationModel<CozinhaModel> {

	@Schema(example = "1")
//    @JsonView(RestauranteViewModel.Resumo.class)
    private Long id;
	
	@Schema(example = "Brasileira")
//    @JsonView(RestauranteViewModel.Resumo.class)
    private String nome;

}
