package com.algaworks.algafoodapi.api.v1.controller;


import com.algaworks.algafoodapi.api.ResourceUriHelper;
import com.algaworks.algafoodapi.api.v1.convert.Converter;
import com.algaworks.algafoodapi.api.v1.model.CidadeModel;
import com.algaworks.algafoodapi.api.v1.model.input.CidadeInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.CidadeControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.core.web.AlgaMediaTypes;
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
@RequestMapping(path = "/v1/cidades", produces = MediaType.APPLICATION_JSON_VALUE)
public class CidadeController  implements CidadeControllerOpenApi {
    @Autowired
    private CadastroCidadeService cadastroCidadeService;

    @Autowired
    private Converter converter;

    @CheckSecurity.Cidades.PodeConsultar
    @GetMapping
    @Override
    public CollectionModel<CidadeModel> getAll() {
        return converter.getCidadeConverter().toCollectionModel(cadastroCidadeService.readAll());
    }


    @CheckSecurity.Cidades.PodeConsultar
    @GetMapping("/{cidadeId}")
    @Override
    public CidadeModel getById(
    		@PathVariable Long cidadeId) {
    	Cidade cidade = cadastroCidadeService.buscarOuFalhar(cidadeId);
        CidadeModel cidadeModel = converter.getCidadeConverter().toModel(cidade);
        return cidadeModel;
    }

    @CheckSecurity.Cidades.PodeEditar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public CidadeModel add( 
    	@RequestBody @Valid CidadeInput cidadeInput) {
        try {
        	Cidade cidade = converter.getCidadeInputConverter().toDomainObject(cidadeInput);
        	var cidadeSalva = cadastroCidadeService.save(cidade);
            CidadeModel cidadeModel = converter.getCidadeConverter().toModel(cidadeSalva);
            
           ResourceUriHelper.addUriResponseHeader(cidadeModel.getId());
            
            return cidadeModel;
        } catch (EstadoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }
 
    @CheckSecurity.Cidades.PodeEditar
    @PutMapping("/{cidadeId}")
    @Override
    public ResponseEntity<?> set(
    		@PathVariable Long cidadeId, @RequestBody @Valid CidadeInput cidadeInput) {
        try {
            Cidade cidadeAtual = cadastroCidadeService.buscarOuFalhar(cidadeId);

            converter.getCidadeInputConverter().copyToDomainObject(cidadeInput, cidadeAtual);
            return ResponseEntity.ok(cadastroCidadeService.save(cidadeAtual));
        } catch (EstadoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @CheckSecurity.Cidades.PodeEditar
    @DeleteMapping("/{cidadeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void remove(
    	@PathVariable Long cidadeId) {
        cadastroCidadeService.deleteById(cidadeId);
    }
}
