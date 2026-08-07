package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
public class UsuarioComSenhaInput extends UsuarioInput {

	@Schema(example = "toor123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String senha;

}
