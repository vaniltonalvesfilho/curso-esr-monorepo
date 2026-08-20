import { HttpErrorResponse } from '@angular/common/http';

import { mensagemDeErro } from './erro';
import { Problema } from './models';

describe('mensagemDeErro', () => {
  it('prefere o user_message do corpo RFC 7807', () => {
    const problema: Problema = {
      status: 400,
      type: 'https://algafood.com.br/dados-invalidos',
      title: 'Dados inválidos',
      detail: 'Um ou mais campos estão inválidos.',
      user_message: 'Confira o preenchimento e tente de novo.',
    };

    const causa = new HttpErrorResponse({ status: 400, error: problema });

    expect(mensagemDeErro(causa)).toBe('Confira o preenchimento e tente de novo.');
  });

  it('cai no detail quando não há user_message', () => {
    const causa = new HttpErrorResponse({
      status: 404,
      error: { status: 404, type: 't', title: 'Não encontrado', detail: 'Cozinha 99 não existe.' },
    });

    expect(mensagemDeErro(causa)).toBe('Cozinha 99 não existe.');
  });

  it('explica o status 0 como API fora do ar em vez de "Erro 0"', () => {
    const causa = new HttpErrorResponse({ status: 0 });

    expect(mensagemDeErro(causa)).toContain('localhost:8080');
  });

  it('trata 403 como falta de permissão', () => {
    const causa = new HttpErrorResponse({ status: 403, error: null });

    expect(mensagemDeErro(causa)).toContain('permissão');
  });
});
