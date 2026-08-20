package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FotoProdutoModel extends RepresentationModel<FotoProdutoModel> {

	@Schema(example = "b8bbd21a-4dd3-4954-835c-3493af2ba6a0_Prime-Rib.jpg")
    private String nomeArquivo;
	
	@Schema(example = "Prime Rib especialidade da casa")
    private String descricao;
	
	@Schema(example = "image/jpeg")
    private String contentType;
	
	@Schema(example = "202912")
    private Long tamanho;

}
