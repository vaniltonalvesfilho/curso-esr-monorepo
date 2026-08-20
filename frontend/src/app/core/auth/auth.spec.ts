import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { Auth } from './auth';

describe('Auth', () => {
  let auth: Auth;
  let http: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    auth = TestBed.inject(Auth);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('começa sem sessão', () => {
    expect(auth.autenticado()).toBe(false);
    expect(auth.accessToken()).toBeNull();
  });

  it('manda o login como form urlencoded, com o marcador de XHR e o cookie', () => {
    void auth.entrar('joao.ger@algafood.com.br', '123');

    const requisicao = http.expectOne(`${environment.apiUrl}/login`);

    expect(requisicao.request.method).toBe('POST');
    // Sem este cabeçalho o servidor responderia o redirect da página de login.
    expect(requisicao.request.headers.get('X-Requested-With')).toBe('XMLHttpRequest');
    // Sem isto o browser descarta o cookie de sessão por ser cross-origin.
    expect(requisicao.request.withCredentials).toBe(true);
    expect(requisicao.request.body).toContain('username=joao.ger%40algafood.com.br');
    expect(requisicao.request.body).toContain('password=123');

    requisicao.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('derruba a sessão do servidor no logout, senão o próximo login entraria sozinho', async () => {
    await Promise.all([
      auth.logout(),
      Promise.resolve().then(() => {
        const requisicao = http.expectOne(`${environment.apiUrl}/logout`);
        expect(requisicao.request.method).toBe('POST');
        expect(requisicao.request.withCredentials).toBe(true);
        requisicao.flush(null, { status: 204, statusText: 'No Content' });
      }),
    ]);

    expect(auth.autenticado()).toBe(false);
  });

  it('rejeita o callback quando o state não confere', async () => {
    sessionStorage.setItem('algafood.pkce.state', 'esperado');
    sessionStorage.setItem('algafood.pkce.code_verifier', 'verifier');

    await expect(auth.concluirLogin('codigo', 'diferente')).rejects.toThrow(/state/i);
  });
});
