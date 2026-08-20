package com.algaworks.algafoodapi.api.v1.controller;

import java.util.List;

import com.algaworks.algafoodapi.api.v1.convert.EstadoConverter;
import com.algaworks.algafoodapi.api.v1.convert.EstadoInputConverter;
import com.algaworks.algafoodapi.api.v1.model.EstadoModel;
import com.algaworks.algafoodapi.api.v1.model.input.EstadoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.EstadoControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.model.Estado;
import com.algaworks.algafoodapi.domain.service.CadastroEstadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v1/estados", produces = MediaType.APPLICATION_JSON_VALUE)
public class EstadoController implements EstadoControllerOpenApi {

    @Autowired
    private CadastroEstadoService cadastroEstadoService;

    @Autowired
    private EstadoConverter estadoConverter;

    @Autowired
    private EstadoInputConverter estadoInputConverter;

    @CheckSecurity.Estados.PodeConsultar
    @GetMapping
    @Override
    public CollectionModel<EstadoModel> getAll() {
        return estadoConverter.toCollectionModel(cadastroEstadoService.buscarTodos());
    }

    @CheckSecurity.Estados.PodeConsultar
    @GetMapping("/{estadoId}")
    @Override
    public EstadoModel getById(@PathVariable Long estadoId) {
        return estadoConverter.toModel(cadastroEstadoService.buscarOuFalhar(estadoId));

    }

    @CheckSecurity.Estados.PodeEditar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public EstadoModel add(@RequestBody @Valid EstadoInput estadoInput) {
    	
    	var estado = estadoInputConverter.toDomainObject(estadoInput);
        return estadoConverter.toModel(cadastroEstadoService.salvar(estado));
      
    }

    @CheckSecurity.Estados.PodeEditar
    @PutMapping("/{estadoId}")
    @Override
    public EstadoModel set(@PathVariable Long estadoId, @RequestBody @Valid EstadoInput estadoInput) {
        var estadoAtual = cadastroEstadoService.buscarOuFalhar(estadoId);
        estadoInputConverter.copyToDomainObject(estadoInput, estadoAtual);
        return estadoConverter.toModel(cadastroEstadoService.salvar(estadoAtual));
    }

    @CheckSecurity.Estados.PodeEditar
    @DeleteMapping("/{estadoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void remove(@PathVariable Long estadoId) {
        cadastroEstadoService.deleteById(estadoId);
    }
}
