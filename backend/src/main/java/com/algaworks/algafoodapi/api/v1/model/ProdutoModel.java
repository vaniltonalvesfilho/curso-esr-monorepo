package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "produtos")
@Setter
@Getter
public class ProdutoModel extends RepresentationModel<ProdutoModel> {

	@Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
	
	@Schema(example = "Prime Rib", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;
	
	@Schema(example = "Carne de qualidade", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricao;
	
	@Schema(example = "42.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal preco;
	
	@Schema(example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean ativo;

}
