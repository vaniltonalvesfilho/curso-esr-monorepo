# AlgaFood Web

SPA em Angular 22 que consome a [AlgaFood API](../backend/). Gerada com o Angular
CLI 22.1.3: standalone, zoneless, Vitest como runner.

## Rodar

```bash
npm install
npm start
```

`http://localhost:4200`. A API precisa estar no ar em `http://localhost:8080` —
veja o [README da raiz](../README.md).

| Comando                     | O que faz                    |
| --------------------------- | ---------------------------- |
| `npm start`                 | dev server com recompilação  |
| `npm run build`             | build de produção em `dist/` |
| `npm test`                  | testes em modo watch         |
| `npx ng test --watch=false` | uma rodada só, para CI       |

## Organização

```
src/app/
├── core/
│   ├── api/      services HTTP, tipos das representações e tradução de erro
│   └── auth/     fluxo PKCE, interceptor do bearer e guard de rota
└── features/     uma pasta por tela (login, callback, restaurantes, …)
```

### `core/api`

Um service por recurso, cada um devolvendo `Observable`. Três detalhes da API que
os tipos em `models.ts` refletem:

- **As propriedades são snake_case, na ida e na volta.** A API define
  `spring.jackson.property-naming-strategy=SNAKE_CASE`, então `taxaFrete` chega
  como `taxa_frete` — e o corpo de um POST precisa mandar `endereco_entrega`, não
  `enderecoEntrega`. Como `fail-on-unknown-properties` está ligado, errar isso
  devolve 400, não é ignorado em silêncio.
- **As respostas são HAL.** Coleções vêm dentro de `_embedded`, sob a chave do
  `collectionRelation` declarado no `@Relation` do model Java (`cozinhas`,
  `restaurantes`, `pedidos`). Quando a coleção está vazia, `_embedded` some da
  resposta — daí o `?? []` nos services.
- **O snake_case também renomeia a relação do HAL.** `formasPagamento` vira
  `formas_pagamento` na chave do `_embedded`. É a única relação da API com mais de
  uma palavra, e errar a chave não dá erro: só devolve lista vazia. Tem teste
  cobrindo em `catalogo.spec.ts`.

`erro.ts` traduz `HttpErrorResponse` para mensagem exibível, preferindo o
`user_message` do corpo RFC 7807 que o `ApiExceptionHandler` da API devolve — e o
`mensagem` do `/login`, que não é recurso da API e não usa RFC 7807.

### `core/auth`

Authorization Code + PKCE feito à mão sobre a Web Crypto API. O porquê de não
usar biblioteca e as travas de instalação do `.npmrc` estão em
[`docs/SEGURANCA-FRONTEND.md`](../docs/SEGURANCA-FRONTEND.md).

A tela de login é do SPA: `entrar()` abre a sessão com um `POST /login` e só então
dispara o `/oauth2/authorize`, que devolve o code direto. O desenho completo e o
que ele implica está no [README da raiz](../README.md#como-o-login-funciona).

O estado da sessão é um `signal` dentro do service `Auth`; `autenticado()`,
`nomeUsuario()` e `permissoes()` são `computed` derivados dele. O token fica em
`sessionStorage`, então fechar a aba encerra a sessão local — mas não a do
servidor, que só cai no `logout()`.

## Configuração

`src/environments/environment.ts` (produção) e `environment.development.ts`
carregam a URL da API e os parâmetros do OAuth. Ao mudar `redirectUri`, registre
o novo valor também no `RegisteredClientSeeder` do backend — o Authorization
Server recusa redirect não registrado.
