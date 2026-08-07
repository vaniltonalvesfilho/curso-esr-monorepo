package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class CidadeInput {

	@Schema(example = "Uberlândia", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String nome;

    @Valid
    @NotNull
    private EstadoIdInput estado;

}
