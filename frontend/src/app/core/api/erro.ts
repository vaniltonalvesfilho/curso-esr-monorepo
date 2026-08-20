import { HttpErrorResponse } from '@angular/common/http';

import { Problema } from './models';

/** Corpo de erro do `/login`, que não é um recurso da API e não usa RFC 7807. */
interface ErroDeLogin {
  mensagem: string;
}

/**
 * Traduz uma falha HTTP para uma mensagem exibível, preferindo o corpo RFC 7807
 * que o `ApiExceptionHandler` da API devolve.
 */
export function mensagemDeErro(causa: unknown): string {
  if (!(causa instanceof HttpErrorResponse)) {
    return causa instanceof Error ? causa.message : 'Falha inesperada.';
  }

  const corpo = causa.error as (Problema & Partial<ErroDeLogin>) | null;

  // O /login responde `{ "mensagem": ... }`; a API responde RFC 7807. Testar o
  // corpo antes do status evita traduzir um login recusado como sessão expirada.
  if (corpo?.mensagem !== undefined) {
    return corpo.mensagem;
  }

  if (causa.status === 0) {
    return 'Não foi possível falar com a API. Confira se ela está no ar em localhost:8080.';
  }

  if (causa.status === 401) {
    return 'Sua sessão expirou ou o token não é mais válido. Entre novamente.';
  }

  if (causa.status === 403) {
    return 'Seu usuário não tem permissão para esta operação.';
  }

  return corpo?.user_message ?? corpo?.detail ?? `Erro ${causa.status} ao chamar a API.`;
}
