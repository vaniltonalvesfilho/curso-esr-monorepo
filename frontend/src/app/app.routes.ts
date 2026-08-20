import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth-guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'restaurantes' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
    // Precisa casar com o redirectUri registrado no client `algafood-web`.
    path: 'authorized',
    loadComponent: () => import('./features/callback/callback').then((m) => m.Callback),
  },
  {
    path: 'restaurantes',
    canActivate: [authGuard],
    loadComponent: () => import('./features/restaurantes/restaurantes').then((m) => m.Restaurantes),
  },
  {
    path: 'cozinhas',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cozinhas/cozinhas').then((m) => m.Cozinhas),
  },
  {
    path: 'pedidos',
    canActivate: [authGuard],
    loadComponent: () => import('./features/pedidos/pedidos').then((m) => m.Pedidos),
  },
  {
    path: 'pedidos/novo',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/pedidos/novo-pedido/novo-pedido').then((m) => m.NovoPedido),
  },
  { path: '**', redirectTo: 'restaurantes' },
];
