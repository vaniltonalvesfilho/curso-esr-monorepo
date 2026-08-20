package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
public class UsuarioInput {

	@Schema(description = "Nome completo do usuário", example = "Manolo Cicrano Fulano da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String nome;

	@Schema(example = "manolo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String email;

}
