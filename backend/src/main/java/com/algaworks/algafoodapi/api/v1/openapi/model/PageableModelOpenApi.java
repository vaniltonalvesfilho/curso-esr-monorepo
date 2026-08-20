package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Schema(name = "Pageable")
@Setter
@Getter
public class PageableModelOpenApi {
	
	@Schema(description = "Número da página (começa em 0)", example = "0")
	private int page;
	
	@Schema(description = "Quantidade de elementos por página", example = "10")
	private int size;
	
	@Schema(description = "Nome da propriedade para ordenação", example = "nome,asc")
	private List<String> sort;
	
}
