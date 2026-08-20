import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Colecao, Restaurante, RestauranteBasico } from './models';

export type ColecaoRestaurantes = Colecao<'restaurantes', RestauranteBasico>;

@Service()
export class RestauranteApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/v1/restaurantes`;

  /** `GET /v1/restaurantes` devolve a projeção básica, sem endereço nem status. */
  listar(): Observable<ColecaoRestaurantes> {
    return this.http.get<ColecaoRestaurantes>(this.url);
  }

  buscar(id: number): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${this.url}/${id}`);
  }

  ativar(id: number): Observable<void> {
    return this.http.put<void>(`${this.url}/${id}/ativo`, null);
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}/ativo`);
  }
}
