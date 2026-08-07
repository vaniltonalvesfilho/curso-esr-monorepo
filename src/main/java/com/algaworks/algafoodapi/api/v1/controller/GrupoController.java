package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.convert.GrupoConverter;
import com.algaworks.algafoodapi.api.v1.convert.GrupoInputConverter;
import com.algaworks.algafoodapi.api.v1.model.GrupoModel;
import com.algaworks.algafoodapi.api.v1.model.input.GrupoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.GrupoControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/grupos", produces = MediaType.APPLICATION_JSON_VALUE)
public class GrupoController implements GrupoControllerOpenApi {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GrupoConverter grupoConverter;

    @Autowired
    private GrupoInputConverter grupoInputConverter;

    @CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
    @GetMapping
    @Override
    public CollectionModel<GrupoModel> getAll() {
        return grupoConverter.toCollectionModel(grupoService.getAll());
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
    @GetMapping("/{grupoId}")
    @Override
    public GrupoModel getById(@PathVariable Long grupoId) {
        return grupoConverter.toModel(grupoService.findOrFail(grupoId));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GrupoModel create(@RequestBody @Valid GrupoInput grupoInput) {
        var grupo = grupoInputConverter.toDomainObject(grupoInput);
        return grupoConverter.toModel(grupoService.save(grupo));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeEditar
    @PutMapping("/{grupoId}")
    @Override
    public GrupoModel update(@PathVariable Long grupoId, @RequestBody @Valid GrupoInput grupoInput) {
        var grupoAtual = grupoService.findOrFail(grupoId);
        grupoInputConverter.copyToDomainObject(grupoInput, grupoAtual);
        return grupoConverter.toModel(grupoService.save(grupoAtual));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeEditar
    @DeleteMapping("/{grupoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable Long grupoId) {
        grupoService.delete(grupoId);
    }
}
