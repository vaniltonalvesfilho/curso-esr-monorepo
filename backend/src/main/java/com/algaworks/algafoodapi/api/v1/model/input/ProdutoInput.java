package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Setter
@Getter
public class ProdutoInput {

    @Schema(example = "Prime Rib", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String nome;

    @Schema(example = "Especialidade da casa", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String descricao;

    @Schema(example = "42.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @PositiveOrZero
    private BigDecimal preco;

    @Schema(example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean ativo;
}
