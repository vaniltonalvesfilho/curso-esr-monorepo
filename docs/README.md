# Documentação da AlgaFood API

| Conteúdo                                                         | Onde                                             |
| ---------------------------------------------------------------- | ------------------------------------------------ |
| Coleção de requisições para o [Bruno](https://www.usebruno.com/) | [`bruno/`](bruno/)                               |
| Verificação de supply chain e travas do npm                      | [`SEGURANCA-FRONTEND.md`](SEGURANCA-FRONTEND.md) |
| OpenAPI 3 gerado em runtime (springdoc)                          | `http://localhost:8080/v3/api-docs`              |
| Swagger UI                                                       | `http://localhost:8080/swagger-ui.html`          |

---

## Coleção Bruno

92 requisições cobrindo toda a API (v1 e v2) mais os endpoints do Spring
Authorization Server, organizadas em 20 pastas.

O Bruno guarda a coleção como arquivos de texto (`.bru`) versionados junto do
código — sem export/import e sem conta na nuvem. É o motivo de ela morar aqui
dentro do repositório.

### Abrir

**Collection → Open Collection** e aponte para `docs/bruno`. Depois selecione o
ambiente **local** no canto superior direito.

Pela CLI:

```bash
npx @usebruno/cli@latest run --env local
```

> Se for usar a CLI, fixe uma versão **≥ 3.2.1**. Versões anteriores foram
> afetadas pela CVE-2026-34841 (comprometimento do `axios` em 31/03/2026).
> O app desktop não foi afetado.

### Subir a API antes

```bash
sudo docker compose up -d algafood-mysql
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=development
```

O perfil `development` carrega a massa de teste via callback
`afterMigrate.sql` do Flyway — é ela que popula os IDs usados como default nas
variáveis de ambiente da coleção.

Para rodar contra o `docker-compose.yml` completo (API atrás do nginx na porta
80), troque para o ambiente **docker**.

---

## Autenticação

A coleção usa **authorization_code + PKCE** com o client público
`algafood-web`, configurado no nível da coleção. Toda requisição herda
(`auth: inherit`), então basta disparar qualquer uma: o Bruno abre o form login
do próprio Authorization Server, você autentica, e o token passa a ser
reutilizado e renovado sozinho.

### Usuários da massa

Senha **`123`** para todos.

| E-mail                          | Grupo              | O que consegue fazer                   |
| ------------------------------- | ------------------ | -------------------------------------- |
| `joao.ger@algafood.com.br`      | Gerente + Vendedor | tudo                                   |
| `maria.vnd@algafood.com.br`     | Vendedor           | consultar tudo + `EDITAR_RESTAURANTES` |
| `jose.aux@algafood.com.br`      | Secretária         | só consultar                           |
| `sebastiao.cad@algafood.com.br` | Cadastrador        | tudo sobre restaurantes                |
| `manoel.loja@example.com`       | —                  | responsável pelos restaurantes 1 e 3   |
| `debora.mendonca@example.com`   | —                  | cliente do pedido da massa             |

Use o **João** para explorar a coleção inteira. Os outros existem para ver o
`@CheckSecurity` recusando: com o **José** um `POST /v1/cozinhas` devolve 403,
e com o **Manoel** o `PUT /v1/restaurantes/1/abertura` passa (é responsável)
enquanto `/v1/restaurantes/2/abertura` não.

### Clients OAuth2

Registrados em código pelo `RegisteredClientSeeder`, não mais em
`oauth_client_details`.

| client_id       | Tipo                         | Grants                            | Segredo          |
| --------------- | ---------------------------- | --------------------------------- | ---------------- |
| `algafood-web`  | público, PKCE obrigatório    | authorization_code, refresh_token | —                |
| `foodanalytics` | confidencial, PKCE + consent | authorization_code, refresh_token | `web123`         |
| `faturamento`   | confidencial                 | client_credentials                | `faturamento123` |

Os dois segredos já vêm preenchidos nos ambientes
(`faturamentoClientSecret`, `foodanalyticsClientSecret`). Não são segredo de
verdade: são valores fixos do curso, e o `RegisteredClientSeeder` os traz em
texto puro no código-fonte. Num cenário real eles sairiam do arquivo de
ambiente.

`algafood-web` é público (`ClientAuthenticationMethod.NONE`), então o
`OAuth2RefreshTokenGenerator` nunca emite refresh token para ele — renovar
significa repetir o authorization_code com PKCE. É por isso que a requisição
_Token - refresh_token_ usa o `foodanalytics`.

---

## Organização

| Pasta                                 | Observação                                                         |
| ------------------------------------- | ------------------------------------------------------------------ |
| 01 Autenticação                       | Endpoints do AS avulsos: metadata, JWKS, token, introspect, revoke |
| 02 Root entry point                   | `/v1` e `/v2`, os pontos de entrada HAL                            |
| 03 Cozinhas                           | Única coleção paginada por `Pageable`                              |
| 04 Estados                            | CRUD                                                               |
| 05 Cidades                            | CRUD (v1)                                                          |
| 06 Formas de pagamento                | Path em camelCase; demonstra ETag / 304                            |
| 07 Grupos                             | CRUD                                                               |
| 08 Permissões                         | Catálogo somente-leitura                                           |
| 09 Grupos → Permissões                | PUT associa, DELETE desassocia                                     |
| 10 Usuários                           | Inclui troca de senha                                              |
| 11 Usuários → Grupos                  | Associação                                                         |
| 12 Restaurantes                       | CRUD + projeção + ativação/abertura, individual e em lote          |
| 13 Restaurantes → Produtos            | Sem DELETE: desativa via `ativo: false`                            |
| 14 Restaurantes → Produtos → Foto     | Upload multipart e content negotiation                             |
| 15 Restaurantes → Formas de pagamento | Associação                                                         |
| 16 Restaurantes → Responsáveis        | Alimenta o `PodeGerenciarFuncionamento`                            |
| 17 Pedidos                            | Identificado por UUID                                              |
| 18 Fluxo do pedido                    | `CRIADO → CONFIRMADO → ENTREGUE`                                   |
| 19 Estatísticas                       | JSON e PDF no mesmo path                                           |
| 20 Cidades V2                         | Contrato diferente da v1                                           |

Cada requisição tem um bloco `docs` com a anotação `@CheckSecurity` exigida e a
armadilha relevante, quando há. As pastas têm um `docs` com o contexto do
recurso.

### Variáveis

Os IDs (`cozinhaId`, `restauranteId`, `pedidoCodigo`, …) vêm no ambiente
apontando para registros da massa de teste, então as requisições funcionam sem
edição. Duas são preenchidas em tempo de execução por scripts de pós-resposta:

- `pedidoCodigo` — sobrescrito ao emitir um pedido novo, para a pasta _Fluxo do
  pedido_ operar sobre ele
- `formaPagamentoListETag` — capturado ao listar formas de pagamento, para a
  requisição de `If-None-Match`
- `accessTokenFaturamento` — capturado no token de client_credentials

### Asserções

A maioria das requisições traz um `assert` de status esperado, então
`bru run --env local` serve como smoke test da API. Não são testes de verdade:
não conferem corpo de resposta e algumas são destrutivas (os `remover.bru`
apagam registros da massa). Rode contra o banco de desenvolvimento, não contra
nada que importe.

---

## Detalhes que economizam tempo

- **Os corpos de requisição são snake_case.** A
  `spring.jackson.property-naming-strategy=SNAKE_CASE` vale também na entrada, e
  `fail-on-unknown-properties` está ligado: `formaPagamento` devolve 400 com
  _"A propriedade 'formaPagamento' não existe"_, o certo é `forma_pagamento`. Seis
  requisições da coleção estavam em camelCase e foram corrigidas em 19/08/2026.
- **No HAL, a relação `formasPagamento` sai como `formas_pagamento`** dentro do
  `_embedded` — o snake_case renomeia a relação junto. É a única relação da API
  com mais de uma palavra.
- **`/v1/formasPagamento` é camelCase**, diferente de todo o resto da API
  (`/v1/formas-pagamento` não existe).
- **Pedido é UUID**, não id numérico. O da massa é
  `f9981ca4-5a5e-4da3-af04-933861df3e55`.
- **Cozinha ou cidade inexistente ao criar restaurante devolve 400**, não 404 —
  é `NegocioException`, porque o recurso ausente está no corpo e não na URI.
- **`PUT /v1/restaurantes/ativacoes` e o DELETE equivalente levam corpo**. Alguns
  proxies descartam body em DELETE; se der 400 atrás do nginx, é isso.
- **`timeOffset` nas estatísticas** tem default `+00:00`. As datas são gravadas
  em UTC (`NORMALIZE_UTC`), então sem passar `-03:00` a virada do dia sai errada
  para o Brasil.
- **A foto do produto tem dois GETs no mesmo path**, resolvidos pelo `Accept`:
  `application/json` devolve o metadado, `image/jpeg` devolve os bytes.
- **`GrupoPermissaoController` tem os nomes dos métodos Java trocados**
  (`desassociar()` chama `associarPermissao()` e vice-versa). A semântica HTTP
  está correta; só os nomes estão invertidos no código.
