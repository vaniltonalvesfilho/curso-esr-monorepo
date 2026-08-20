package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.AlgaLinks;
import com.algaworks.algafoodapi.api.v1.convert.FormaPagamentoConverter;
import com.algaworks.algafoodapi.api.v1.convert.RestauranteInputConverter;
import com.algaworks.algafoodapi.api.v1.model.FormaPagamentoModel;
import com.algaworks.algafoodapi.api.v1.openapi.controller.RestauranteFormaPagControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.service.CadastroRestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/restaurantes/{restauranteId}/formas-pagamento", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestauranteFormaPagamentoController implements RestauranteFormaPagControllerOpenApi {

    @Autowired
    private CadastroRestauranteService restauranteService;

    @Autowired
    private FormaPagamentoConverter convert;

    @Autowired
    private RestauranteInputConverter inputConvert;
    
    @Autowired
    private AlgaLinks algaLinks;

    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping
    @Override
    public CollectionModel<FormaPagamentoModel> listar(@PathVariable Long restauranteId) {
        var restaurante = restauranteService.buscarOuFalhar(restauranteId);

        CollectionModel<FormaPagamentoModel> formasPagamentoModel = 
        		convert.toCollectionModel(restaurante.getFormasPagamento())
        		.removeLinks()
        		.add(algaLinks.linkToRestauranteFormasPagamento(restauranteId))
        		.add(algaLinks.linkToRestauranteFormaPagamentoAssociacao(restauranteId, "associar"));
        
        formasPagamentoModel.getContent().forEach(formaPagamentoModel -> {
        	formaPagamentoModel.add(algaLinks
        			.linkToRestauranteFormaPagamentoDesassociacao(
        					restauranteId, formaPagamentoModel.getId(), "desassociar"));
        });
        
        return formasPagamentoModel;
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @DeleteMapping("{formaPagamentoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> desassociar(@PathVariable Long restauranteId, @PathVariable Long formaPagamentoId) {
        restauranteService.desassociarFormaPagamento(restauranteId, formaPagamentoId);
        
        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @PutMapping("{formaPagamentoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> associar(@PathVariable Long restauranteId, @PathVariable Long formaPagamentoId) {
        restauranteService.associarFormaPagamento(restauranteId, formaPagamentoId);
        
        return ResponseEntity.noContent().build();
    }

}
