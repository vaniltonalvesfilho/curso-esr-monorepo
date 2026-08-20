# Segurança das dependências do frontend

Registro da verificação feita em **19/08/2026**, antes da primeira instalação de
dependências npm neste repositório, e das travas que ficaram configuradas.

## Por que isso existe

O ecossistema npm está sob uma campanha contínua de ataques de cadeia de
suprimentos desde setembro de 2025 — a família **Shai-Hulud**, um worm que rouba
credenciais de quem instala o pacote e usa essas credenciais para publicar
versões maliciosas de outros pacotes, se espalhando sozinho.

Ondas relevantes até esta data:

| Data         | Campanha                                       | Alcance                                                                 |
| ------------ | ---------------------------------------------- | ----------------------------------------------------------------------- |
| set/2025     | Shai-Hulud (original)                          | 796 pacotes, 132 mi de downloads/mês                                    |
| mar/2026     | Axios                                          | roubo de tokens npm e PATs do GitHub                                    |
| abr/2026     | Shai-Hulud: The Third Coming / Mini Shai-Hulud | TanStack, Mistral AI, UiPath, OpenSearch, Bitwarden CLI                 |
| mai/2026     | Mini Shai-Hulud                                | ecossistema AntV, `timeago.js`; 639 versões maliciosas em uma hora      |
| jun/2026     | Miasma                                         | 32 pacotes de `@redhat-cloud-services`                                  |
| jul/2026     | miasma-train-p1                                | repositórios do AsyncAPI, via branch de pré-produção desprotegida       |
| **ago/2026** | **CHAINDROP**                                  | **400+ pacotes, a partir da conta do mantenedor de `keyv`/`cacheable`** |

A onda de agosto é a que importava aqui: começou em **4/08/2026**, quinze dias
antes desta verificação, e atingiu `keyv`, `cacheable-request`, `cache-manager`,
`flat-cache` e `file-entry-cache` — pacotes que aparecem como dependência
transitiva em boa parte das toolchains de frontend.

## O que foi verificado

A árvore de dependências do Angular 22 foi resolvida **sem instalar nada**, com
`npm install --package-lock-only --ignore-scripts`, que baixa apenas metadados do
registry e não executa código. O resultado foi cruzado com a lista de IoC
publicada pela Wiz (446 pacotes, 2317 versões maliciosas).

| Checagem                                                         | Resultado                                                                                                              |
| ---------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| Versões maliciosas ainda resolvíveis no registry                 | Não. `keyv@6.0.0`, `cacheable-request@13.0.20`, `cache-manager@7.2.10` e `file-entry-cache@11.1.4` foram despublicadas |
| Versões maliciosas na árvore do Angular                          | **Nenhuma**                                                                                                            |
| Pacotes da família comprometida presentes, mesmo em versão limpa | Nenhum — o Angular não usa `keyv`/`cacheable`                                                                          |
| `npm audit` após a instalação                                    | 0 vulnerabilidades                                                                                                     |
| Pacotes que executam script de instalação                        | 5: `esbuild`, `lmdb`, `@parcel/watcher`, `msgpackr-extract`, `fsevents`                                                |

Para reproduzir a checagem, a lista de IoC está em
<https://github.com/wiz-sec-public/wiz-research-iocs/blob/main/reports/keyv-packages.csv>.

## As travas em `frontend/.npmrc`

### `ignore-scripts=true`

Impede a execução de `preinstall`/`postinstall` de qualquer dependência. É o
vetor que as campanhas Shai-Hulud usam para rodar o payload de roubo de
credenciais no momento do `npm install`.

Os cinco pacotes com script de instalação da árvore são bindings nativos
legítimos, e todos distribuem binários por `optionalDependencies` — o build e os
testes passam com os scripts desligados (verificado). Se algum dia um binário
nativo faltar, libere só aquele pacote, nominalmente:

```bash
npm rebuild esbuild
```

Nunca desligue a trava inteira com `npm install --ignore-scripts=false`.

### `min-release-age=7`

Só instala versões publicadas há mais de 7 dias. Uma release comprometida
costuma ser detectada e despublicada em horas, então a carência cobre justamente
a janela cega em que ninguém sabe ainda.

Isso não é hipotético neste projeto: no dia da instalação, o `@angular/cli@22.1.5`
tinha **3 horas de publicado**. A carência fez a resolução cair para o 22.1.3, de
5/08 — igualmente livre de IoC e já observado pela comunidade por duas semanas.

O custo é ficar alguns dias atrás do último patch. Para um projeto de estudo,
é troca barata.

### `audit=true` + `audit-level=high`

Faz o `npm install` reclamar de vulnerabilidade conhecida de severidade alta ou
crítica. Cobre CVE publicado, que é problema diferente de pacote comprometido —
as duas travas acima não pegam isso.

## Rotina ao adicionar uma dependência nova

1. `npm install <pacote>` — as travas do `.npmrc` valem automaticamente.
2. Confira que o `package-lock.json` mudou só no que você esperava.
3. Commite o lockfile junto. Em CI, use `npm ci`, que instala exatamente o que
   está no lockfile e nunca reresolve versões.

Se precisar instalar algo com urgência, antes da carência de 7 dias, prefira
`--before` com uma data explícita a desligar a trava — e registre o motivo.
