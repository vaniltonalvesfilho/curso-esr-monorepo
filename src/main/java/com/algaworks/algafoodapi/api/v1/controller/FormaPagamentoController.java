package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.convert.FormaPagamentoConverter;
import com.algaworks.algafoodapi.api.v1.convert.FormaPagamentoInputConverter;
import com.algaworks.algafoodapi.api.v1.model.FormaPagamentoModel;
import com.algaworks.algafoodapi.api.v1.model.input.FormaPagamentoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.FormaPagamentoControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafoodapi.domain.service.FormaPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/v1/formasPagamento", produces = MediaType.APPLICATION_JSON_VALUE)
public class FormaPagamentoController implements FormaPagamentoControllerOpenApi {

    @Autowired
    private FormaPagamentoService formaPagamentoService;

    @Autowired
    private FormaPagamentoRepository formaPagamentoRepository;
    
    @Autowired
    private FormaPagamentoConverter formaPagamentoConverter;

    @Autowired
    private FormaPagamentoInputConverter formaPagamentoInputConverter;

    @CheckSecurity.FormasPagamento.PodeConsultar
    @GetMapping
    @Override
    public ResponseEntity<CollectionModel<FormaPagamentoModel>> getAll(ServletWebRequest request) {
    	
    	ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());
    	
    	String eTag = "0";
    	OffsetDateTime dataUltimaAtualizacao = formaPagamentoRepository.getDataUltimaAtualizacao();
    	
    	if (dataUltimaAtualizacao != null) {
    		eTag = String.valueOf(dataUltimaAtualizacao.toEpochSecond());
    	}
    	
    	if (request.checkNotModified(eTag)) {
    		return null;
    	}
    	
    	var formasPagamento = formaPagamentoService.getAll();
    	
        var formasPagamentoModel = formaPagamentoConverter.toCollectionModel(formasPagamento);
        
        return ResponseEntity.ok()
        		.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
        		.eTag(eTag)
        		.body(formasPagamentoModel);
        
    }

    @CheckSecurity.FormasPagamento.PodeConsultar
    @GetMapping("/{formaPagamentoId}")
    @Override
    public ResponseEntity<FormaPagamentoModel> getById(@PathVariable Long formaPagamentoId, ServletWebRequest request) {
        
    	ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());
    	
    	String eTag = "0";
    	OffsetDateTime dataUltimaAtualizacao = formaPagamentoRepository.getDataUltimaAtualizacaoById(formaPagamentoId);
    	
    	if (dataUltimaAtualizacao != null) {
    		eTag = String.valueOf(dataUltimaAtualizacao.toEpochSecond());
    	}
    	
    	if (request.checkNotModified(eTag)) {
    		return null;
    	}
    	
    	
    	var formaPagamento = formaPagamentoService.buscarOuFalhar(formaPagamentoId);
        var formaPagmentoModel = formaPagamentoConverter.toModel(formaPagamento);
        
        return ResponseEntity.ok()
        		.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
        		.eTag(eTag)
        		.body(formaPagmentoModel);
        
    }

    @CheckSecurity.FormasPagamento.PodeEditar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public FormaPagamentoModel create(@RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
        var formaPagamento = formaPagamentoInputConverter.toDomainObject(formaPagamentoInput);
        return formaPagamentoConverter.toModel(formaPagamentoService.salvar(formaPagamento));
    }

    @CheckSecurity.FormasPagamento.PodeEditar
    @PutMapping("/{formaPagamentoId}")
    @Override
    public FormaPagamentoModel update(@PathVariable Long formaPagamentoId, @RequestBody @Valid FormaPagamentoInput formaPagamentoInput) {
        var formaPagamentoAtual = formaPagamentoService.buscarOuFalhar(formaPagamentoId);
        formaPagamentoInputConverter.copyToDomainObject(formaPagamentoInput, formaPagamentoAtual);
        return formaPagamentoConverter.toModel(formaPagamentoService.salvar(formaPagamentoAtual));
    }

    @CheckSecurity.FormasPagamento.PodeEditar
    @DeleteMapping("/{formaPagamentoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable Long formaPagamentoId) {
        formaPagamentoService.delete(formaPagamentoId);
    }
}
