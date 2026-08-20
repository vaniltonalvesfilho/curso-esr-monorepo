package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
public class SenhaInput {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "toor123")
    @NotBlank
    private String senhaAtual;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123toor")
    @NotBlank
    private String novaSenha;

}
