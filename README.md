# Curso Especialista Spring REST

Durante este curso aprendi muito a como modelar e implementar REST APIs.

## De onde veio, e o que mudou

O ponto de partida é o código do curso **Especialista Spring REST** (AlgaWorks),
publicado em
[github.com/vaniltonalvesfilho/curso-spring-esr](https://github.com/vaniltonalvesfilho/curso-spring-esr)
— o `initial commit` de 2021 que abre este histórico: só a AlgaFood API, em
Spring Boot 2.4.9 e Java 15.

Daí para cá o repositório virou monorepo e o código andou. O que mudou, item a
item:

### Backend: do Boot 2.4 ao Boot 3.5

| Item                    | Antes (curso, 2021)                                   | Agora                                                        |
| ----------------------- | ----------------------------------------------------- | ------------------------------------------------------------ |
| Spring Boot             | 2.4.9                                                 | 3.5.16                                                        |
| Java                    | 15                                                    | 21                                                            |
| Namespace               | `javax.*`                                             | `jakarta.*` (Jakarta EE 10)                                   |
| ORM                     | Hibernate 5                                           | Hibernate 6 — dialeto fixo, `OffsetDateTime` em `NORMALIZE_UTC` |
| Driver MySQL            | `mysql:mysql-connector-java`                          | `com.mysql:mysql-connector-j` + `flyway-mysql`                |
| Documentação da API     | SpringFox 3.0.0, anotações Swagger 2                  | springdoc-openapi 2.8.17, anotações OpenAPI 3                 |
| Authorization server    | `spring-security-oauth2` 2.3.8 + `spring-security-jwt` | **Spring Authorization Server 1.5.8** / Spring Security 6.5.11 |
| Grant do cliente web    | `password`                                            | `authorization_code` + PKCE (client público)                  |
| Filtro de campos        | Squiggly 1.3.18                                       | fork `dev.nicklasw` 2.0.1 (jakarta, ANTLR shaded)             |
| Imagem Docker           | `dockerfile-maven-plugin`                             | buildpacks do `spring-boot-maven-plugin`, base `temurin:21-jre-alpine` |
| Sessão distribuída      | `spring.redis.*`, `store-type`                        | `spring.data.redis.*` + exclusão da `SessionAutoConfiguration` |

Três trocas mereceram substituição de projeto, não só bump de versão: o
**SpringFox** parou no 3.0.0 e nunca migrou para `jakarta`; o
**`spring-security-oauth2`** foi descontinuado e não roda no Spring Security 6 —
como o grant `password` não existe no OAuth 2.1, o client `algafood-web` virou
público com PKCE; e o **Squiggly** original arrasta `antlr4-runtime` 4.6, que
conflita com o ANTLR 4.13 exigido pelo Hibernate 6.

Uma trava proposital: o `jackson-bom` está fixo em 2.19.4. O Jackson 2.20 removeu
`PropertyNamingStrategy.PropertyNamingStrategyBase`, que o `Jackson2HalModule` do
spring-hateoas 2.5.3 ainda referencia — sem o pin, toda resposta HAL quebra com
`NoClassDefFoundError`.

### Bugs que a migração revelou

Nem tudo era incompatibilidade; três defeitos estavam lá desde o curso:

- `AccessDeniedException` era importada de `java.nio.file` no exception handler.
  O handler nunca era acionado e **todo acesso negado virava 500 em vez de 403**.
- `spring.flyway.locations` apontava para `db/testdata`, que não existe. O
  callback `afterMigrate.sql` nunca rodava e **o banco de desenvolvimento subia
  vazio**.
- `application-production.properties` trazia valores sensíveis com default no
  arquivo. Agora todos vêm de variável de ambiente, sem default.

### Frontend: não existia

O curso é só de API. A SPA em `frontend/` foi escrita do zero em **Angular 22 /
TypeScript 6, zoneless**, com as telas de login, restaurantes, cozinhas, listagem
de pedidos e montagem de pedido. O Authorization Code + PKCE é implementado sobre
a Web Crypto API, sem biblioteca OAuth de terceiro — o fluxo está detalhado
[mais abaixo](#como-o-login-funciona). O `.npmrc` traz travas contra ataques de
cadeia de suprimentos.

### Desenvolvimento assistido por IA

A migração e o frontend foram feitos em par com o
[Claude Code](https://claude.com/claude-code); os commits desse trabalho trazem
`Co-Authored-By: Claude`. A IA escreveu o diff — as decisões de arquitetura, a
revisão e os testes que provam que funciona são meus.

O repositório é um monorepo com dois módulos independentes:

| Pasta                    | O que é                                       | Stack                                                        |
| ------------------------ | --------------------------------------------- | ------------------------------------------------------------ |
| [`backend/`](backend/)   | AlgaFood API — a API de delivery do curso     | Java 21, Spring Boot 3.5, MySQL, Spring Authorization Server |
| [`frontend/`](frontend/) | AlgaFood Web — SPA que consome a API          | Angular 22, TypeScript 6, zoneless                           |
| [`docs/`](docs/)         | Coleção Bruno e notas de segurança            | —                                                            |
| [`nginx/`](nginx/)       | Proxy reverso usado pelo `docker-compose.yml` | —                                                            |

---

## Subir o backend

```bash
sudo docker compose up -d algafood-mysql
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=development
```

A API sobe em `http://localhost:8080`. O perfil `development` carrega a massa de
teste pelo `afterMigrate.sql` do Flyway e desliga o Spring Session (não há Redis
fora do Docker).

> O `Dockerfile` mora em `backend/` agora. Para construir a imagem:
> `docker build -t vaniltonalv3sfilho/algafood-api backend/`.

## Subir o frontend

```bash
cd frontend
npm install
npm start
```

A SPA sobe em `http://localhost:4200` e fala com a API em `localhost:8080` — os
dois precisam estar no ar. Entre com um usuário da massa de teste (por exemplo
`joao.ger@algafood.com.br`, senha `123`); a lista completa está em
[`docs/README.md`](docs/README.md#usuários-da-massa).

### Como o login funciona

A tela de login é a do próprio SPA — a página do Authorization Server não aparece
para quem usa o front. Por baixo, continua **Authorization Code + PKCE** com o
client público `algafood-web`:

1. o SPA manda e-mail e senha num `POST /login` (`X-Requested-With: XMLHttpRequest`,
   `withCredentials`), que abre a sessão e responde **204**;
2. com a sessão de pé, o SPA navega para `/oauth2/authorize`, que — sem consent
   configurado — devolve o code na hora, sem renderizar formulário nenhum;
3. o servidor redireciona para `http://localhost:4200/authorized`;
4. a rota `authorized` troca o code pelo access token e guarda em `sessionStorage`.

O token continua vindo pelo authorization_code; o que mudou é só quem desenha a
tela. **Isso só vale porque SPA e Authorization Server são da mesma casa** — um
client de terceiro não deve ver a senha do usuário, e para esses o redirect à
tela do servidor é justamente o ponto.

O servidor decide o formato da resposta do `/login` pelo cabeçalho
`X-Requested-With`: com ele, 204/401 para o SPA; sem ele, o redirect de sempre —
que é o que o Swagger UI e a coleção Bruno usam. Os dois fluxos continuam
funcionando lado a lado.

O `logout` derruba a sessão do servidor além do token local. Sem isso o cookie
continuaria de pé e o próximo "Entrar" passaria direto, sem pedir senha.

O client é público, então **não há refresh token** — quando o access token vence
(6 h), a renovação é um novo authorization_code. É limitação do
`OAuth2RefreshTokenGenerator` para clients `ClientAuthenticationMethod.NONE`, não
um esquecimento.

O PKCE é implementado sobre a Web Crypto API, sem biblioteca OAuth de terceiro —
uma dependência a menos na árvore npm, pelos motivos do documento de segurança
abaixo.

#### CORS

Como o login manda o cookie de sessão cross-origin, a API não pode mais responder
`Access-Control-Allow-Origin: *` — o browser recusa curinga em requisição com
credencial, e um curinga deixaria qualquer site chamar a API com a sessão de quem
estivesse logado. As origens liberadas são uma lista explícita:

```properties
algafood.cors.allowed-origins=http://localhost:4200
```

Em produção, se o SPA e a API ficarem em domínios registráveis diferentes, o
cookie de sessão vai precisar de `SameSite=None; Secure` para sobreviver ao
`POST /login`.

### Fazer um pedido

`/pedidos/novo` monta o pedido em uma tela só: escolher o restaurante carrega o
cardápio e as formas de pagamento dele, os itens vão para um carrinho com
quantidade e observação, e o total soma a taxa de frete do restaurante.

Trocar de restaurante limpa o carrinho e a forma de pagamento de propósito: a API
recusa com 400 uma forma de pagamento que não esteja associada àquele
restaurante.

### Segurança das dependências

`frontend/.npmrc` traz três travas contra ataques de cadeia de suprimentos
(`ignore-scripts`, `min-release-age`, `audit-level`). O que elas fazem, por que
existem e o que fazer ao adicionar uma dependência nova está em
[`docs/SEGURANCA-FRONTEND.md`](docs/SEGURANCA-FRONTEND.md).

Em CI, use `npm ci` — instala exatamente o que está no `package-lock.json`.

---

## Comandos por módulo

| Ação                                 | Comando                                                     |
| ------------------------------------ | ----------------------------------------------------------- |
| Testes do backend                    | `cd backend && ./mvnw test`                                 |
| Empacotar o backend                  | `cd backend && ./mvnw package`                              |
| Testes do frontend                   | `cd frontend && npm test`                                   |
| Build de produção do frontend        | `cd frontend && npm run build`                              |
| Smoke test da API pela coleção Bruno | `cd docs/bruno && npx @usebruno/cli@latest run --env local` |
