import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ColecaoPaginada, Pedido, PedidoResumo } from './models';

export type PaginaPedidos = ColecaoPaginada<'pedidos', PedidoResumo>;

/** Espelha o `PedidoFilter` do backend. */
export interface FiltroPedido {
  clienteId?: number;
  restauranteId?: number;
  /** ISO-8601 com offset, ex.: `2026-08-01T00:00:00Z`. */
  dataCriacaoInicio?: string;
  dataCriacaoFim?: string;
}

/**
 * Corpo de `POST /v1/pedidos`, espelhando o `PedidoInput` do backend.
 *
 * <p>As chaves são snake_case porque a `property-naming-strategy` da API também
 * vale na desserialização, e `fail-on-unknown-properties` está ligado: mandar
 * `enderecoEntrega` em vez de `endereco_entrega` devolve 400, não é ignorado.
 *
 * <p>O cliente não vem no corpo — o backend o tira do `usuario_id` do token.
 */
export interface NovoPedido {
  restaurante: { id: number };
  forma_pagamento: { id: number };
  endereco_entrega: {
    cep: string;
    logradouro: string;
    numero: string;
    complemento?: string;
    bairro: string;
    cidade: { id: number };
  };
  itens: {
    produto_id: number;
    quantidade: number;
    observacao?: string;
  }[];
}

@Service()
export class PedidoApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/v1/pedidos`;

  pesquisar(filtro: FiltroPedido = {}, pagina = 0, tamanho = 10): Observable<PaginaPedidos> {
    let params = new HttpParams().set('page', pagina).set('size', tamanho);

    for (const [chave, valor] of Object.entries(filtro)) {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(chave, String(valor));
      }
    }

    return this.http.get<PaginaPedidos>(this.url, { params });
  }

  emitir(pedido: NovoPedido): Observable<Pedido> {
    return this.http.post<Pedido>(this.url, pedido);
  }
}
