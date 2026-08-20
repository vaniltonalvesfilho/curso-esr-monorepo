package com.algaworks.algafoodapi.domain.service;

import com.algaworks.algafoodapi.domain.filter.VendaDiariaFilter;

public interface VendaReportService {

    byte[] emitirVendasDiarias(VendaDiariaFilter filter, String timeOffset);

}
