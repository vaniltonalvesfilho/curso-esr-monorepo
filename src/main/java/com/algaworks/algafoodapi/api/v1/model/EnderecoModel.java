package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Relation(collectionRelation = "enderecos")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class EnderecoModel extends RepresentationModel<EnderecoModel> {
	
	@Schema(example = "40000000")
    private String cep;

	@Schema(example = "Rua Número Zero")
    private String logradouro;

	@Schema(example = "42")
    private String numero;
	
	@Schema(example = "Próximo ao centro")
    private String complemento;

	@Schema(example = "Barro Vinicius de Morais")
    private String bairro;

    private CidadeResumoModel cidade;
}
