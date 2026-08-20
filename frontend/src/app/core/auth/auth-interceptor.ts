import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { Auth } from './auth';

/**
 * Anexa o bearer token nas chamadas à API.
 *
 * <p>Só nas chamadas à API: o endpoint de token do Authorization Server tem a
 * mesma origem e mandar um Authorization nele faria o servidor interpretar como
 * autenticação de client, quebrando o fluxo do client público.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const ehChamadaDeApi = req.url.startsWith(`${environment.apiUrl}/v1`);

  if (!ehChamadaDeApi) {
    return next(req);
  }

  const accessToken = inject(Auth).accessToken();

  if (accessToken === null) {
    return next(req);
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${accessToken}` } }));
};
