package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "permissoes")
@Setter
@Getter
public class PermissaoModel extends RepresentationModel<PermissaoModel> {
	
	@Schema(description = "id", example = "1")
    private Long id;
	@Schema(description = "nome", example = "EDITAR_RESTAURANTE")
    private String nome;
	@Schema(description = "descricao", example = "Permite editar dados do restaurante")
    private String descricao;

}
