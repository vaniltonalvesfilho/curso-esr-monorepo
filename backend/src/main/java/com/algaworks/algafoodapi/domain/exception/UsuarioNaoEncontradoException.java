package com.algaworks.algafoodapi.domain.exception;


public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontradoException(Long id) {
        this(String.format("Não existe cadastro de usuário com id %d",id));
    }
}
