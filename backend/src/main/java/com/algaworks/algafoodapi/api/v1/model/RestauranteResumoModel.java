package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "restaurantes")
@Setter
@Getter
public class RestauranteResumoModel extends RepresentationModel<RestauranteResumoModel> {

	@Schema(example = "1")
    private Long id;
	
	@Schema(example = "Thai Gourmet")
    private String nome;

}
