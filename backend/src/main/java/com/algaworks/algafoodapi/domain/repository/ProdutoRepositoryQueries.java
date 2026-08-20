package com.algaworks.algafoodapi.domain.repository;

import com.algaworks.algafoodapi.domain.model.FotoProduto;

public interface ProdutoRepositoryQueries {

    FotoProduto save(FotoProduto fotoProduto);
    void delete(FotoProduto foto);
}
