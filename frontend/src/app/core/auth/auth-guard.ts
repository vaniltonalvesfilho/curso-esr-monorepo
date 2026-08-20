import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { Auth } from './auth';

export const authGuard: CanActivateFn = (_rota, estado) => {
  const auth = inject(Auth);

  if (auth.autenticado()) {
    return true;
  }

  // Guarda o destino para o callback voltar para onde a pessoa tentou ir.
  return inject(Router).createUrlTree(['/login'], {
    queryParams: { destino: estado.url },
  });
};
