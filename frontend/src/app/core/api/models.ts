/**
 * Tipos das representações da API v1.
 *
 * <p>Os nomes das propriedades são snake_case porque a API define
 * `spring.jackson.property-naming-strategy=SNAKE_CASE` — o `taxaFrete` do Java
 * chega no JSON como `taxa_frete`.
 */

export interface Link {
  href: string;
  templated?: boolean;
}

export type Links = Record<string, Link | Link[]>;

export interface RecursoHal {
  _links?: Links;
}

/** Metadados de página do Spring HATEOAS (`PagedModel`). */
export interface PaginaMeta {
  size: number;
  total_elements: number;
  total_pages: number;
  number: number;
}

/**
 * `PagedModel<T>` do Spring HATEOAS. `Chave` é o `collectionRelation` declarado
 * no `@Relation` do model no backend — é a chave dentro de `_embedded`.
 */
export interface ColecaoPaginada<Chave extends string, T> extends RecursoHal {
  _embedded?: Record<Chave, T[]>;
  page: PaginaMeta;
}

/** `CollectionModel<T>`: mesma forma, sem os metadados de página. */
export interface Colecao<Chave extends string, T> extends RecursoHal {
  _embedded?: Record<Chave, T[]>;
}

export interface Cozinha extends RecursoHal {
  id: number;
  nome: string;
}

export interface CidadeResumo extends RecursoHal {
  id: number;
  nome: string;
  estado: string;
}

export interface Estado extends RecursoHal {
  id: number;
  nome: string;
}

/** Representação completa de `GET /v1/cidades`, com o estado aninhado. */
export interface Cidade extends RecursoHal {
  id: number;
  nome: string;
  estado: Estado;
}

export interface FormaPagamento extends RecursoHal {
  id: number;
  descricao: string;
}

export interface Produto extends RecursoHal {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  ativo: boolean;
}

export interface Endereco {
  cep: string;
  logradouro: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade?: CidadeResumo;
}

/** Projeção devolvida por `GET /v1/restaurantes`. */
export interface RestauranteBasico extends RecursoHal {
  id: number;
  nome: string;
  /** `BigDecimal` no Java, número no JSON. */
  taxa_frete: number;
  cozinha: Cozinha;
}

/** Representação completa devolvida por `GET /v1/restaurantes/{id}`. */
export interface Restaurante extends RestauranteBasico {
  ativo: boolean;
  aberto: boolean;
  endereco?: Endereco;
}

export interface Usuario extends RecursoHal {
  id: number;
  nome: string;
  email: string;
}

export interface PedidoResumo extends RecursoHal {
  codigo: string;
  status: string;
  subtotal: number;
  taxa_frete: number;
  valor_total: number;
  /** ISO-8601 com offset (`OffsetDateTime`). */
  data_criacao: string;
  restaurante: { id: number; nome: string };
  cliente: Usuario;
}

export interface ItemPedido {
  produto_id: number;
  produto_nome: string;
  quantidade: number;
  preco_unitario: number;
  preco_total: number;
  observacao?: string;
}

/** Representação completa, devolvida por `GET /v1/pedidos/{codigo}` e pelo POST. */
export interface Pedido extends PedidoResumo {
  data_confirmacao?: string;
  data_cancelamento?: string;
  data_entrega?: string;
  endereco_entrega: Endereco;
  forma_pagamento: FormaPagamento;
  itens: ItemPedido[];
}

/** Corpo de erro da API (RFC 7807, montado pelo `ApiExceptionHandler`). */
export interface Problema {
  status: number;
  timestamp?: string;
  type: string;
  title: string;
  detail: string;
  user_message?: string;
  objects?: { name: string; user_message: string }[];
}
