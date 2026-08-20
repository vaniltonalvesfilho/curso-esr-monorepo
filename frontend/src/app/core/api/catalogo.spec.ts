import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { CatalogoApi } from './catalogo';
import { FormaPagamento, Produto } from './models';

describe('CatalogoApi', () => {
  let api: CatalogoApi;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    api = TestBed.inject(CatalogoApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lê as formas de pagamento da chave formas_pagamento, não de formasPagamento', () => {
    let recebidas: FormaPagamento[] = [];
    api.formasDePagamentoDoRestaurante(1).subscribe((formas) => (recebidas = formas));

    // Resposta real da API: o snake_case renomeia a relação do @Relation.
    http
      .expectOne((req) => req.url.endsWith('/v1/restaurantes/1/formas-pagamento'))
      .flush({
        _embedded: {
          formas_pagamento: [
            { id: 1, descricao: 'Cartão de crédito' },
            { id: 3, descricao: 'Dinheiro' },
          ],
        },
      });

    expect(recebidas.map((f) => f.descricao)).toEqual(['Cartão de crédito', 'Dinheiro']);
  });

  it('devolve lista vazia quando _embedded não vem na resposta', () => {
    let recebidos: Produto[] = [];
    api.produtosDoRestaurante(2).subscribe((produtos) => (recebidos = produtos));

    // A API omite _embedded em coleção vazia, em vez de mandar um array vazio.
    http.expectOne((req) => req.url.endsWith('/v1/restaurantes/2/produtos')).flush({ _links: {} });

    expect(recebidos).toEqual([]);
  });
});
