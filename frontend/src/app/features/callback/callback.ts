import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Auth } from '../../core/auth/auth';

/**
 * Recebe o redirect do Authorization Server em `/authorized`, troca o code pelo
 * token e sai da rota. Não fica no histórico com o code na URL.
 */
@Component({
  selector: 'app-callback',
  imports: [],
  templateUrl: './callback.html',
  styleUrl: './callback.scss',
})
export class Callback implements OnInit {
  private readonly rota = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(Auth);

  protected readonly erro = signal<string | null>(null);

  async ngOnInit(): Promise<void> {
    const params = this.rota.snapshot.queryParamMap;

    // O servidor devolve `error` quando a autorização é negada.
    const erroOauth = params.get('error');
    if (erroOauth !== null) {
      this.erro.set(`${erroOauth}: ${params.get('error_description') ?? 'autorização negada'}`);
      return;
    }

    const code = params.get('code');
    const state = params.get('state');

    if (code === null || state === null) {
      this.erro.set('O servidor não devolveu code e state.');
      return;
    }

    try {
      const destino = await this.auth.concluirLogin(code, state);
      await this.router.navigateByUrl(destino, { replaceUrl: true });
    } catch (causa) {
      this.erro.set(causa instanceof Error ? causa.message : 'Falha ao trocar o code pelo token.');
    }
  }

  protected voltarParaLogin(): void {
    void this.router.navigate(['/login'], { replaceUrl: true });
  }
}
