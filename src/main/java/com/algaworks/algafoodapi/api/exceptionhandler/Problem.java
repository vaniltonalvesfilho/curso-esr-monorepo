package com.algaworks.algafoodapi.api.exceptionhandler;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "Problema")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class Problem {
 
	@Schema(example = "400")
    private Integer status;
	
	@Schema(example = "2021-06-20T18:47:52.190955Z")
	private OffsetDateTime timestamp;
	
	@Schema(example = "https://algafood.com.br/dados-invalidos")
    private String type;
	
	@Schema(example = "Dados inválidos")
    private String title;
	
	@Schema(example = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente")
    private String detail;
	
	@Schema(example = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente")
    private String userMessage;
  
    @Schema(description = "Objetos ou campos que geraram os erros")
    private List<Object> objects;

    @Schema(name = "ObjetoProblema")
    @Getter
    @Builder
    public static class Object {

    	@Schema(example = "nome")
        private String name;
    	
    	@Schema(example = "Nome do restaurante é obrigatório")
        private String userMessage;
        
    }

}
