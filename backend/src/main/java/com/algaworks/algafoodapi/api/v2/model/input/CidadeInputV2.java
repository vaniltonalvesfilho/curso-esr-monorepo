package com.algaworks.algafoodapi.api.v2.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CidadeInput")
@Setter
@Getter
public class CidadeInputV2 {

	@Schema(example = "Uberlândia", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String nomeCidade;

	@Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Long idEstado;

}
