import { Component, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { mensagemDeErro } from '../../core/api/erro';
import { Auth } from '../../core/auth/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  /** Preenchido pelo authGuard via query param, com `withComponentInputBinding()`. */
  readonly destino = input('/restaurantes');

  private readonly auth = inject(Auth);

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', Validators.required],
  });

  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected async entrar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.erro.set(null);

    const { email, senha } = this.formulario.getRawValue();

    try {
      // Em caso de sucesso isto não retorna: o browser sai para o /oauth2/authorize.
      await this.auth.entrar(email, senha, this.destino());
    } catch (causa) {
      this.erro.set(mensagemDeErro(causa));
      this.enviando.set(false);
    }
  }
}
