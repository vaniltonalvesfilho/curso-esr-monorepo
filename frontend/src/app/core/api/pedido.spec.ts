import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { NovoPedido, PedidoApi } from './pedido';

describe('PedidoApi', () => {
  let api: PedidoApi;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    api = TestBed.inject(PedidoApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('emite o pedido com as chaves em snake_case que a API espera', () => {
    const pedido: NovoPedido = {
      restaurante: { id: 1 },
      forma_pagamento: { id: 1 },
      endereco_entrega: {
        cep: '38400-000',
        logradouro: 'Rua Floriano Peixoto',
        numero: '500',
        bairro: 'Brasil',
        cidade: { id: 1 },
      },
      itens: [{ produto_id: 1, quantidade: 2 }],
    };

    api.emitir(pedido).subscribe();

    const requisicao = http.expectOne(`${environment.apiUrl}/v1/pedidos`);
    expect(requisicao.request.method).toBe('POST');

    // A API roda com fail-on-unknown-properties, então camelCase aqui vira 400.
    const corpo = requisicao.request.body as Record<string, unknown>;
    expect(Object.keys(corpo)).toContain('endereco_entrega');
    expect(Object.keys(corpo)).toContain('forma_pagamento');
    expect(Object.keys(corpo)).not.toContain('enderecoEntrega');
    expect(Object.keys(corpo)).not.toContain('formaPagamento');
    expect(Object.keys((corpo['itens'] as Record<string, unknown>[])[0])).toContain('produto_id');

    requisicao.flush({ codigo: 'abc' });
  });

  it('monta a paginação e descarta filtro vazio', () => {
    api.pesquisar({ restauranteId: 3, clienteId: undefined }, 2, 20).subscribe();

    const requisicao = http.expectOne(
      (req) => req.url === `${environment.apiUrl}/v1/pedidos` && req.method === 'GET',
    );

    expect(requisicao.request.params.get('page')).toBe('2');
    expect(requisicao.request.params.get('size')).toBe('20');
    expect(requisicao.request.params.get('restauranteId')).toBe('3');
    expect(requisicao.request.params.has('clienteId')).toBe(false);

    requisicao.flush({ page: { size: 20, total_elements: 0, total_pages: 0, number: 2 } });
  });
});
