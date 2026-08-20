package com.algaworks.algafoodapi.api.v2.controller;

import io.swagger.v3.oas.annotations.media.Schema;

import com.algaworks.algafoodapi.api.ResourceUriHelper;
import com.algaworks.algafoodapi.api.v1.openapi.controller.CidadeControllerOpenApi;
import com.algaworks.algafoodapi.api.v2.assembler.CidadeConverterV2;
import com.algaworks.algafoodapi.api.v2.assembler.CidadeInputConverterV2;
import com.algaworks.algafoodapi.api.v2.model.CidadeModelV2;
import com.algaworks.algafoodapi.api.v2.model.input.CidadeInputV2;
import com.algaworks.algafoodapi.api.v2.openapi.controller.CidadeControllerOpenApiV2;
import com.algaworks.algafoodapi.domain.exception.EstadoNaoEncontradoException;
import com.algaworks.algafoodapi.domain.exception.NegocioException;
import com.algaworks.algafoodapi.domain.model.Cidade;
import com.algaworks.algafoodapi.domain.service.CadastroCidadeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v2/cidades", produces = MediaType.APPLICATION_JSON_VALUE)
public class CidadeControllerV2 implements CidadeControllerOpenApiV2 {
    @Autowired
    private CadastroCidadeService cadastroCidadeService;

    @Autowired
    private CidadeConverterV2 converter;
    
    @Autowired
    private CidadeInputConverterV2 inputConverter;

    
    @GetMapping
    public CollectionModel<CidadeModelV2> getAll() {
        return converter.toCollectionModel(cadastroCidadeService.readAll());
    }

    
    @GetMapping("/{cidadeId}")
    public CidadeModelV2 getById(
    		@PathVariable Long cidadeId) {
    	Cidade cidade = cadastroCidadeService.buscarOuFalhar(cidadeId);
        CidadeModelV2 cidadeModel = converter.toModel(cidade);
        return cidadeModel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CidadeModelV2 add( 
    	@RequestBody @Valid CidadeInputV2 cidadeInput) {
        try {
        	Cidade cidade = inputConverter.toDomainObject(cidadeInput);
        	var cidadeSalva = cadastroCidadeService.save(cidade);
            CidadeModelV2 cidadeModel = converter.toModel(cidadeSalva);
            
           ResourceUriHelper.addUriResponseHeader(cidadeModel.getIdCidade());
            
            return cidadeModel;
        } catch (EstadoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }
 
    
    @PutMapping("/{cidadeId}")
    public ResponseEntity<?> set(
    		@PathVariable Long cidadeId, @RequestBody @Valid CidadeInputV2 cidadeInput) {
        try {
            Cidade cidadeAtual = cadastroCidadeService.buscarOuFalhar(cidadeId);

            inputConverter.copyToDomainObject(cidadeInput, cidadeAtual);
            return ResponseEntity.ok(cadastroCidadeService.save(cidadeAtual));
        } catch (EstadoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @DeleteMapping("/{cidadeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
    	@PathVariable Long cidadeId) {
        cadastroCidadeService.deleteById(cidadeId);
    }
}
