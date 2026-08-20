package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.convert.ProdutoConvert;
import com.algaworks.algafoodapi.api.v1.convert.ProdutoInputConvert;
import com.algaworks.algafoodapi.api.v1.model.ProdutoModel;
import com.algaworks.algafoodapi.api.v1.model.input.ProdutoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.RestauranteProdutoControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.model.Produto;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import com.algaworks.algafoodapi.domain.service.CadastroRestauranteService;
import com.algaworks.algafoodapi.domain.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/restaurantes/{restauranteId}/produtos", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestauranteProdutoController implements RestauranteProdutoControllerOpenApi {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CadastroRestauranteService cadastroRestauranteService;

    @Autowired
    private ProdutoConvert produtoConvert;

    @Autowired
    private ProdutoInputConvert produtoInputConvert;
    
    @Autowired
    private AlgaLinks algaLinks;


    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping
    @Override
    public CollectionModel<ProdutoModel> getAll(@PathVariable Long restauranteId,
                                     @RequestParam(required = false, defaultValue = "false") Boolean incluirInativos) {
        Restaurante restaurante = cadastroRestauranteService.buscarOuFalhar(restauranteId);
        List<Produto> allProdutos = null;

        if (incluirInativos) {
            allProdutos = produtoService.getAll(restaurante);
        } else {
            allProdutos = produtoService.getAllActive(restaurante);
        }
        return produtoConvert.toCollectionModel(allProdutos)
        			.add(algaLinks.linkToProdutos(restauranteId));

    }

    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping("/{produtoId}")
    @Override
    public ProdutoModel getById(@PathVariable Long restauranteId, @PathVariable Long produtoId) {
//        cadastroRestauranteService.buscarOuFalhar(restauranteId);
        Produto produto = produtoService.findOrFail(restauranteId, produtoId);
        return produtoConvert.toModel(produto);
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @PostMapping
    @Override
    public ProdutoModel create(@PathVariable Long restauranteId, @RequestBody @Valid ProdutoInput produtoInput) {
        Restaurante restaurante = cadastroRestauranteService.buscarOuFalhar(restauranteId);
        Produto produto = produtoInputConvert.toDomainObject(produtoInput);
        produto.setRestaurante(restaurante);
        return produtoConvert.toModel(produtoService.save(produto));
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @PutMapping("/{produtoId}")
    @Override
    public ProdutoModel update(@PathVariable Long restauranteId, @PathVariable Long produtoId, @RequestBody @Valid ProdutoInput produtoInput) {

        Produto produtoAtual = produtoService.findOrFail(restauranteId, produtoId);
        produtoInputConvert.copyToDomainObject(produtoInput, produtoAtual);
        return produtoConvert.toModel(produtoService.save(produtoAtual));

    }
}
