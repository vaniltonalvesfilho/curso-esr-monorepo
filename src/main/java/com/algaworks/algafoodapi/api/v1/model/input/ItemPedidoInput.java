package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Setter
@Getter
public class ItemPedidoInput {

	@Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long produtoId;

	@Schema(example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @PositiveOrZero
    private Integer quantidade;

	@Schema(example = "Muito acebolado por favor")
    private String observacao;

}
