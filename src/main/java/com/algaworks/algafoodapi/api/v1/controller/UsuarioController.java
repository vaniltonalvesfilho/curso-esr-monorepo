package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.convert.UsuarioConverter;
import com.algaworks.algafoodapi.api.v1.convert.UsuarioInputConverter;
import com.algaworks.algafoodapi.api.v1.model.UsuarioModel;
import com.algaworks.algafoodapi.api.v1.model.input.SenhaInput;
import com.algaworks.algafoodapi.api.v1.model.input.UsuarioComSenhaInput;
import com.algaworks.algafoodapi.api.v1.model.input.UsuarioInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.UsuarioControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController implements UsuarioControllerOpenApi {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioConverter usuarioConverter;

    @Autowired
    private UsuarioInputConverter usuarioInputConverter;

    @CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
    @GetMapping
    public CollectionModel<UsuarioModel> getAll() {
        return usuarioConverter.toCollectionModel(usuarioService.getAll());
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
    @GetMapping("/{usuarioId}")
    public UsuarioModel getById(@PathVariable Long usuarioId) {
        return usuarioConverter.toModel(usuarioService.findOrFail(usuarioId));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeEditar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioModel create(@RequestBody @Valid UsuarioComSenhaInput usuarioComSenhaInput) {
        var usuario = usuarioInputConverter.toDomainObject(usuarioComSenhaInput);
        return usuarioConverter.toModel(usuarioService.save(usuario));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeAtualizarUsuario
    @PutMapping("/{usuarioId}")
    public UsuarioModel update(@PathVariable Long usuarioId, @RequestBody @Valid UsuarioInput usuarioInput) {
        var usuarioAtual = usuarioService.findOrFail(usuarioId);
        usuarioInputConverter.copyToDomainObject(usuarioInput, usuarioAtual);
        return usuarioConverter.toModel(usuarioService.save(usuarioAtual));
    }

    @CheckSecurity.UsuariosGruposPermissoes.PodeAlterarSenha
    @PutMapping("/{usuarioId}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSenha(@PathVariable Long usuarioId, @RequestBody @Valid SenhaInput senhaInput) {
        usuarioService.updatePassword(usuarioId, senhaInput.getSenhaAtual(), senhaInput.getNovaSenha());
    }
}
