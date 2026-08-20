package com.algaworks.algafoodapi.domain.service;

import com.algaworks.algafoodapi.domain.exception.ProdutoNaoEncontradoException;
import com.algaworks.algafoodapi.domain.model.Produto;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import com.algaworks.algafoodapi.domain.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> getAllActive(Restaurante restaurante) {
        return produtoRepository.findByRestauranteAndAtivoTrue(restaurante);
    }

    public List<Produto> getAll(Restaurante restaurante) {
        return produtoRepository.findByRestaurante(restaurante);
    }

    public Produto findOrFail(Long restauranteId, Long produtoId) {
        return produtoRepository.findById(restauranteId, produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId, restauranteId));
    }

    @Transactional
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

}
