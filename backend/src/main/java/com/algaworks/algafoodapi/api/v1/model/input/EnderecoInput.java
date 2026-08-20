package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class EnderecoInput {

	@Schema(example = "40000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String cep;

	@Schema(example = "Rua Número Zero", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String logradouro;

	@Schema(example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String numero;

	@Schema(example = "Próximo ao centro")
    private String complemento;

	@Schema(example = "Barro Vinicius de Morais", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bairro;

    @Valid
    @NotNull
    private CidadeIdInput cidade;
}
