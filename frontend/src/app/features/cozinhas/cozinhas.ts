import { Component, computed, inject, OnInit, signal } from '@angular/core';

import { CozinhaApi } from '../../core/api/cozinha';
import { mensagemDeErro } from '../../core/api/erro';
import { Cozinha, PaginaMeta } from '../../core/api/models';

@Component({
  selector: 'app-cozinhas',
  imports: [],
  templateUrl: './cozinhas.html',
  styleUrl: './cozinhas.scss',
})
export class Cozinhas implements OnInit {
  private readonly api = inject(CozinhaApi);

  protected readonly cozinhas = signal<Cozinha[]>([]);
  protected readonly pagina = signal<PaginaMeta | null>(null);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);

  protected readonly temAnterior = computed(() => (this.pagina()?.number ?? 0) > 0);
  protected readonly temProxima = computed(() => {
    const pagina = this.pagina();
    return pagina !== null && pagina.number + 1 < pagina.total_pages;
  });

  ngOnInit(): void {
    this.carregar(0);
  }

  protected carregar(numeroDaPagina: number): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.api.listar(numeroDaPagina).subscribe({
      next: (colecao) => {
        this.cozinhas.set(colecao._embedded?.cozinhas ?? []);
        this.pagina.set(colecao.page);
        this.carregando.set(false);
      },
      error: (causa) => {
        this.erro.set(mensagemDeErro(causa));
        this.carregando.set(false);
      },
    });
  }

  protected irPara(delta: number): void {
    this.carregar((this.pagina()?.number ?? 0) + delta);
  }
}
