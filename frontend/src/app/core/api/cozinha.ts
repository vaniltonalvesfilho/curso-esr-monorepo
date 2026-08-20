import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ColecaoPaginada, Cozinha } from './models';

export type PaginaCozinhas = ColecaoPaginada<'cozinhas', Cozinha>;

@Service()
export class CozinhaApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/v1/cozinhas`;

  listar(pagina = 0, tamanho = 10): Observable<PaginaCozinhas> {
    const params = new HttpParams().set('page', pagina).set('size', tamanho);

    return this.http.get<PaginaCozinhas>(this.url, { params });
  }

  buscar(id: number): Observable<Cozinha> {
    return this.http.get<Cozinha>(`${this.url}/${id}`);
  }
}
