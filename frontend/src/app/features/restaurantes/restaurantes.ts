import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';

import { mensagemDeErro } from '../../core/api/erro';
import { RestauranteBasico } from '../../core/api/models';
import { RestauranteApi } from '../../core/api/restaurante';

@Component({
  selector: 'app-restaurantes',
  imports: [DecimalPipe],
  templateUrl: './restaurantes.html',
  styleUrl: './restaurantes.scss',
})
export class Restaurantes implements OnInit {
  private readonly api = inject(RestauranteApi);

  protected readonly restaurantes = signal<RestauranteBasico[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.carregar();
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.api.listar().subscribe({
      next: (colecao) => {
        // `_embedded` some da resposta quando a coleção está vazia.
        this.restaurantes.set(colecao._embedded?.restaurantes ?? []);
        this.carregando.set(false);
      },
      error: (causa) => {
        this.erro.set(mensagemDeErro(causa));
        this.carregando.set(false);
      },
    });
  }
}
