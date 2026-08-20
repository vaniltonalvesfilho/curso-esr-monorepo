import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Cidade, Colecao, FormaPagamento, Produto } from './models';

/**
 * Listagens auxiliares usadas para montar um pedido: os produtos e as formas de
 * pagamento de um restaurante, e as cidades para o endereço de entrega.
 *
 * <p>Os três desembrulham o `_embedded` do HAL e devolvem o array direto — quem
 * consome só quer a lista, e `_embedded` some da resposta quando ela está vazia.
 */
@Service()
export class CatalogoApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/v1`;

  /** Só os produtos ativos: `incluirInativos` fica no default `false`. */
  produtosDoRestaurante(restauranteId: number): Observable<Produto[]> {
    return this.http
      .get<Colecao<'produtos', Produto>>(`${this.url}/restaurantes/${restauranteId}/produtos`)
      .pipe(map((colecao) => colecao._embedded?.produtos ?? []));
  }

  /**
   * A chave do `_embedded` aqui é `formas_pagamento`, e não o `formasPagamento`
   * declarado no `@Relation` do model: a `property-naming-strategy` snake_case da
   * API também renomeia a relação do HAL. É a única relação da API com mais de
   * uma palavra, então é a única onde os dois nomes divergem — e errar a chave
   * não dá erro, só devolve lista vazia.
   */
  formasDePagamentoDoRestaurante(restauranteId: number): Observable<FormaPagamento[]> {
    return this.http
      .get<Colecao<'formas_pagamento', FormaPagamento>>(
        `${this.url}/restaurantes/${restauranteId}/formas-pagamento`,
      )
      .pipe(map((colecao) => colecao._embedded?.formas_pagamento ?? []));
  }

  cidades(): Observable<Cidade[]> {
    return this.http
      .get<Colecao<'cidades', Cidade>>(`${this.url}/cidades`)
      .pipe(map((colecao) => colecao._embedded?.cidades ?? []));
  }
}
