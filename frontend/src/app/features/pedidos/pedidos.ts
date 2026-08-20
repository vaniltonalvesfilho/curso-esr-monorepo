import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { mensagemDeErro } from '../../core/api/erro';
import { PedidoResumo } from '../../core/api/models';
import { PedidoApi } from '../../core/api/pedido';

@Component({
  selector: 'app-pedidos',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.scss',
})
export class Pedidos implements OnInit {
  private readonly api = inject(PedidoApi);

  /** Código do pedido recém-emitido, vindo do redirect da tela de novo pedido. */
  readonly emitido = input('');

  protected readonly pedidos = signal<PedidoResumo[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.carregar();
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.api.pesquisar().subscribe({
      next: (colecao) => {
        this.pedidos.set(colecao._embedded?.pedidos ?? []);
        this.carregando.set(false);
      },
      error: (causa) => {
        this.erro.set(mensagemDeErro(causa));
        this.carregando.set(false);
      },
    });
  }
}
