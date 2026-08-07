package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.algaworks.algafoodapi.api.v1.model.EstatisticaModel;
import com.algaworks.algafoodapi.domain.filter.VendaDiariaFilter;
import com.algaworks.algafoodapi.domain.model.insight.VendaDiaria;

@Tag(name = "Estatísticas")
public interface EstatistcaControllerOpenApi {

	@Operation(summary = "Estatísticas", hidden = true)
	public EstatisticaModel estatisticas();

	@Operation(summary = "Consulta estatísticas de vendas diárias")
    @Parameters({
        @Parameter(name = "restauranteId", description = "ID do restaurante", example = "1", schema = @Schema(type = "integer", format = "int64")),
        @Parameter(name = "dataCriacaoInicio", description = "Data/hora inicial da criação do pedido", example = "2019-12-01T00:00:00Z", schema = @Schema(type = "string", format = "date-time")),
        @Parameter(name = "dataCriacaoFim", description = "Data/hora final da criação do pedido", example = "2019-12-02T23:59:59Z", schema = @Schema(type = "string", format = "date-time"))
    })
    public List<VendaDiaria> consultarVendasDiarias(
            VendaDiariaFilter filter,
            @RequestParam(required = false, defaultValue = "+00:00") String timeOffset);

    public ResponseEntity<byte[]> consultarVendasDiariasPdf(
            VendaDiariaFilter filter,
            @RequestParam(required = false, defaultValue = "+00:00") String timeOffset);
}
