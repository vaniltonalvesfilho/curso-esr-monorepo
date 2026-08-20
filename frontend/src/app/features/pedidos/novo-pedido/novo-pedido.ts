import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { CatalogoApi } from '../../../core/api/catalogo';
import { mensagemDeErro } from '../../../core/api/erro';
import { Cidade, FormaPagamento, Produto, RestauranteBasico } from '../../../core/api/models';
import { NovoPedido as CorpoNovoPedido, PedidoApi } from '../../../core/api/pedido';
import { RestauranteApi } from '../../../core/api/restaurante';

interface ItemDoCarrinho {
  produto: Produto;
  quantidade: number;
  observacao: string;
}

@Component({
  selector: 'app-novo-pedido',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './novo-pedido.html',
  styleUrl: './novo-pedido.scss',
})
export class NovoPedido implements OnInit {
  private readonly restauranteApi = inject(RestauranteApi);
  private readonly catalogoApi = inject(CatalogoApi);
  private readonly pedidoApi = inject(PedidoApi);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly restaurantes = signal<RestauranteBasico[]>([]);
  protected readonly produtos = signal<Produto[]>([]);
  protected readonly formasPagamento = signal<FormaPagamento[]>([]);
  protected readonly cidades = signal<Cidade[]>([]);

  protected readonly itens = signal<ItemDoCarrinho[]>([]);
  protected readonly restauranteSelecionado = signal<RestauranteBasico | null>(null);

  protected readonly carregandoCardapio = signal(false);
  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly subtotal = computed(() =>
    this.itens().reduce((soma, item) => soma + item.produto.preco * item.quantidade, 0),
  );

  protected readonly taxaFrete = computed(() => this.restauranteSelecionado()?.taxa_frete ?? 0);

  protected readonly total = computed(() => this.subtotal() + this.taxaFrete());

  protected readonly formulario = this.fb.nonNullable.group({
    restauranteId: [0, Validators.min(1)],
    formaPagamentoId: [0, Validators.min(1)],
    cep: ['', Validators.required],
    logradouro: ['', Validators.required],
    numero: ['', Validators.required],
    complemento: [''],
    bairro: ['', Validators.required],
    cidadeId: [0, Validators.min(1)],
  });

  /** O produto escolhido no seletor de "adicionar item", fora do formulário. */
  protected readonly produtoParaAdicionar = signal(0);

  ngOnInit(): void {
    this.restauranteApi.listar().subscribe({
      next: (colecao) => this.restaurantes.set(colecao._embedded?.restaurantes ?? []),
      error: (causa) => this.erro.set(mensagemDeErro(causa)),
    });

    this.catalogoApi.cidades().subscribe({
      next: (cidades) => this.cidades.set(cidades),
      error: (causa) => this.erro.set(mensagemDeErro(causa)),
    });
  }

  /**
   * Produtos e formas de pagamento pertencem ao restaurante, então trocar de
   * restaurante invalida o carrinho e a forma escolhida — a API recusa forma de
   * pagamento não associada ao restaurante com 400.
   */
  protected aoTrocarRestaurante(valorSelecionado: string): void {
    // O id vem do evento, e não do control: a ordem entre o listener do
    // SelectControlValueAccessor e o `(change)` do template não é garantida, e
    // ler o control poderia pegar o restaurante anterior.
    const id = Number(valorSelecionado);

    this.itens.set([]);
    this.produtoParaAdicionar.set(0);
    this.formulario.controls.formaPagamentoId.setValue(0);
    this.produtos.set([]);
    this.formasPagamento.set([]);
    this.restauranteSelecionado.set(this.restaurantes().find((r) => r.id === id) ?? null);

    if (id <= 0) {
      return;
    }

    this.carregandoCardapio.set(true);
    this.erro.set(null);

    this.catalogoApi.produtosDoRestaurante(id).subscribe({
      next: (produtos) => {
        this.produtos.set(produtos);
        this.carregandoCardapio.set(false);
      },
      error: (causa) => {
        this.erro.set(mensagemDeErro(causa));
        this.carregandoCardapio.set(false);
      },
    });

    this.catalogoApi.formasDePagamentoDoRestaurante(id).subscribe({
      next: (formas) => this.formasPagamento.set(formas),
      error: (causa) => this.erro.set(mensagemDeErro(causa)),
    });
  }

  protected adicionarItem(): void {
    const produto = this.produtos().find((p) => p.id === Number(this.produtoParaAdicionar()));

    if (produto === undefined) {
      return;
    }

    const jaNoCarrinho = this.itens().find((item) => item.produto.id === produto.id);

    if (jaNoCarrinho !== undefined) {
      this.alterarQuantidade(produto.id, jaNoCarrinho.quantidade + 1);
    } else {
      this.itens.update((itens) => [...itens, { produto, quantidade: 1, observacao: '' }]);
    }

    this.produtoParaAdicionar.set(0);
  }

  protected alterarQuantidade(produtoId: number, quantidade: number): void {
    if (quantidade < 1) {
      this.removerItem(produtoId);
      return;
    }

    this.itens.update((itens) =>
      itens.map((item) => (item.produto.id === produtoId ? { ...item, quantidade } : item)),
    );
  }

  protected alterarObservacao(produtoId: number, observacao: string): void {
    this.itens.update((itens) =>
      itens.map((item) => (item.produto.id === produtoId ? { ...item, observacao } : item)),
    );
  }

  protected removerItem(produtoId: number): void {
    this.itens.update((itens) => itens.filter((item) => item.produto.id !== produtoId));
  }

  protected emitir(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    if (this.itens().length === 0) {
      this.erro.set('Adicione ao menos um item ao pedido.');
      return;
    }

    this.enviando.set(true);
    this.erro.set(null);

    const valores = this.formulario.getRawValue();

    const corpo: CorpoNovoPedido = {
      restaurante: { id: Number(valores.restauranteId) },
      forma_pagamento: { id: Number(valores.formaPagamentoId) },
      endereco_entrega: {
        cep: valores.cep,
        logradouro: valores.logradouro,
        numero: valores.numero,
        bairro: valores.bairro,
        cidade: { id: Number(valores.cidadeId) },
        // Campo opcional: mandar string vazia passaria no @NotBlank ausente, mas
        // gravaria lixo. Só vai quando tem conteúdo.
        ...(valores.complemento.trim() === '' ? {} : { complemento: valores.complemento.trim() }),
      },
      itens: this.itens().map((item) => ({
        produto_id: item.produto.id,
        quantidade: item.quantidade,
        ...(item.observacao.trim() === '' ? {} : { observacao: item.observacao.trim() }),
      })),
    };

    this.pedidoApi.emitir(corpo).subscribe({
      next: (pedido) => {
        this.enviando.set(false);
        void this.router.navigate(['/pedidos'], { queryParams: { emitido: pedido.codigo } });
      },
      error: (causa) => {
        this.erro.set(mensagemDeErro(causa));
        this.enviando.set(false);
      },
    });
  }
}
