package com.algaworks.algafoodapi.domain.exception;

public class FormaPagamentoNaoEncontradoException extends EntidadeNaoEncontradaException {


    public FormaPagamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public FormaPagamentoNaoEncontradoException(Long id) {
        this(String.format("Não existe cadastro de forma de pagamento com código %d", id));
    }
}
