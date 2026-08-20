import { computed, inject, Service, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';

interface RespostaToken {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope?: string;
}

interface Sessao {
  accessToken: string;
  /** Epoch em milissegundos. */
  expiraEm: number;
}

interface ClaimsToken {
  sub?: string;
  nome_completo?: string;
  usuario_id?: string;
  authorities?: string[];
  scope?: string;
  exp?: number;
}

const CHAVE_SESSAO = 'algafood.sessao';
const CHAVE_VERIFIER = 'algafood.pkce.code_verifier';
const CHAVE_STATE = 'algafood.pkce.state';
const CHAVE_DESTINO = 'algafood.destino_pos_login';

/**
 * Authorization Code + PKCE contra o Spring Authorization Server da API.
 *
 * <p>Implementado sobre a Web Crypto API do próprio browser em vez de uma
 * biblioteca OAuth. O fluxo é curto o bastante para não justificar mais uma
 * dependência de terceiro na árvore npm — que é exatamente a superfície de
 * ataque que a configuração de segurança do `.npmrc` tenta reduzir.
 *
 * <p>A credencial é digitada na tela do próprio SPA, que abre a sessão com um
 * POST em `/login` antes de disparar o `/oauth2/authorize`. Com a sessão de pé e
 * `requireAuthorizationConsent(false)`, o servidor devolve o code na hora, sem
 * mostrar formulário nenhum. O token continua vindo pelo authorization_code —
 * o que muda é só quem desenha a tela de login.
 *
 * <p>Isso só é aceitável porque SPA e Authorization Server são da mesma casa. Um
 * client de terceiro nunca deveria ver a senha do usuário; para esses, o
 * redirect para a tela do servidor é justamente o ponto.
 *
 * <p>O client `algafood-web` é público (`ClientAuthenticationMethod.NONE`), então
 * o Authorization Server nunca emite refresh token para ele. Quando o access
 * token expira, a renovação é um novo authorization_code — não há refresh.
 */
@Service()
export class Auth {
  private readonly http = inject(HttpClient);

  private readonly sessao = signal<Sessao | null>(lerSessaoArmazenada());

  readonly autenticado = computed(() => {
    const sessao = this.sessao();
    return sessao !== null && sessao.expiraEm > Date.now();
  });

  readonly accessToken = computed(() => (this.autenticado() ? this.sessao()!.accessToken : null));

  readonly claims = computed<ClaimsToken | null>(() => {
    const token = this.accessToken();
    return token === null ? null : decodificarClaims(token);
  });

  readonly nomeUsuario = computed(() => this.claims()?.nome_completo ?? this.claims()?.sub ?? null);

  readonly permissoes = computed(() => this.claims()?.authorities ?? []);

  temPermissao(permissao: string): boolean {
    return this.permissoes().includes(permissao);
  }

  /**
   * Abre a sessão no servidor com as credenciais digitadas na tela do SPA e, se
   * der certo, segue para o `/oauth2/authorize`.
   *
   * @throws HttpErrorResponse com status 401 quando e-mail ou senha não conferem.
   */
  async entrar(email: string, senha: string, destinoPosLogin = '/restaurantes'): Promise<void> {
    const credenciais = new URLSearchParams({ username: email, password: senha });

    await firstValueFrom(
      this.http.post(`${environment.apiUrl}/login`, credenciais.toString(), {
        headers: new HttpHeaders({
          'Content-Type': 'application/x-www-form-urlencoded',
          // Faz o servidor responder 204/401 em vez do redirect da página de login.
          'X-Requested-With': 'XMLHttpRequest',
        }),
        // Sem isso o browser descarta o cookie de sessão por ser cross-origin.
        withCredentials: true,
        observe: 'response',
      }),
    );

    await this.iniciarLogin(destinoPosLogin);
  }

  /**
   * Gera o par verifier/challenge, guarda o verifier e manda o browser para o
   * endpoint de autorização. Não retorna: a navegação sai da aplicação.
   */
  async iniciarLogin(destinoPosLogin = '/restaurantes'): Promise<void> {
    const codeVerifier = gerarStringAleatoria(64);
    const state = gerarStringAleatoria(16);

    sessionStorage.setItem(CHAVE_VERIFIER, codeVerifier);
    sessionStorage.setItem(CHAVE_STATE, state);
    sessionStorage.setItem(CHAVE_DESTINO, destinoPosLogin);

    const parametros = new URLSearchParams({
      response_type: 'code',
      client_id: environment.oauth.clientId,
      redirect_uri: environment.oauth.redirectUri,
      scope: environment.oauth.scope,
      state,
      code_challenge: await gerarCodeChallenge(codeVerifier),
      code_challenge_method: 'S256',
    });

    window.location.href = `${environment.oauth.issuer}/oauth2/authorize?${parametros}`;
  }

  /**
   * Troca o authorization code pelo access token. Devolve para onde navegar.
   *
   * @throws Error se o `state` não bater com o que foi enviado (proteção contra
   *         CSRF no callback) ou se o verifier tiver sumido do sessionStorage.
   */
  async concluirLogin(code: string, state: string): Promise<string> {
    const stateEsperado = sessionStorage.getItem(CHAVE_STATE);
    const codeVerifier = sessionStorage.getItem(CHAVE_VERIFIER);

    sessionStorage.removeItem(CHAVE_STATE);
    sessionStorage.removeItem(CHAVE_VERIFIER);

    if (stateEsperado === null || state !== stateEsperado) {
      throw new Error('O state devolvido não confere com o enviado. Refaça o login.');
    }

    if (codeVerifier === null) {
      throw new Error('O code_verifier do PKCE não está mais na sessão. Refaça o login.');
    }

    const corpo = new URLSearchParams({
      grant_type: 'authorization_code',
      code,
      redirect_uri: environment.oauth.redirectUri,
      client_id: environment.oauth.clientId,
      code_verifier: codeVerifier,
    });

    const resposta = await firstValueFrom(
      this.http.post<RespostaToken>(`${environment.oauth.issuer}/oauth2/token`, corpo.toString(), {
        headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
      }),
    );

    const sessao: Sessao = {
      accessToken: resposta.access_token,
      // Um minuto de folga para não mandar uma requisição com token já vencido.
      expiraEm: Date.now() + (resposta.expires_in - 60) * 1000,
    };

    sessionStorage.setItem(CHAVE_SESSAO, JSON.stringify(sessao));
    this.sessao.set(sessao);

    const destino = sessionStorage.getItem(CHAVE_DESTINO) ?? '/restaurantes';
    sessionStorage.removeItem(CHAVE_DESTINO);

    return destino;
  }

  /**
   * Descarta o token local e encerra a sessão no servidor.
   *
   * <p>Derrubar a sessão importa: sem isso o cookie continuaria de pé e o próximo
   * "Entrar" passaria direto pelo `/oauth2/authorize`, entrando de novo sem pedir
   * senha — o que pareceria que o logout não funcionou.
   *
   * <p>A falha no `/logout` é engolida de propósito. O token local já foi
   * descartado, então do ponto de vista de quem clicou a saída aconteceu; travar
   * a tela porque o servidor não respondeu seria pior.
   */
  async logout(): Promise<void> {
    sessionStorage.removeItem(CHAVE_SESSAO);
    this.sessao.set(null);

    try {
      await firstValueFrom(
        this.http.post(`${environment.apiUrl}/logout`, null, {
          headers: new HttpHeaders({ 'X-Requested-With': 'XMLHttpRequest' }),
          withCredentials: true,
          observe: 'response',
        }),
      );
    } catch {
      // Sessão do servidor pode já ter expirado; nada a fazer.
    }
  }
}

function lerSessaoArmazenada(): Sessao | null {
  const bruto = sessionStorage.getItem(CHAVE_SESSAO);

  if (bruto === null) {
    return null;
  }

  try {
    const sessao = JSON.parse(bruto) as Sessao;
    return sessao.expiraEm > Date.now() ? sessao : null;
  } catch {
    return null;
  }
}

function decodificarClaims(token: string): ClaimsToken | null {
  const payload = token.split('.')[1];

  if (payload === undefined) {
    return null;
  }

  try {
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    // O payload é UTF-8; sem esse passo os acentos do nome do usuário viram lixo.
    const texto = new TextDecoder().decode(Uint8Array.from(json, (c) => c.charCodeAt(0)));
    return JSON.parse(texto) as ClaimsToken;
  } catch {
    return null;
  }
}

function gerarStringAleatoria(bytes: number): string {
  return base64Url(crypto.getRandomValues(new Uint8Array(bytes)));
}

async function gerarCodeChallenge(codeVerifier: string): Promise<string> {
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(codeVerifier));
  return base64Url(new Uint8Array(digest));
}

function base64Url(bytes: Uint8Array): string {
  return btoa(String.fromCharCode(...bytes))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}
