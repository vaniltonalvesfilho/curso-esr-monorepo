package com.algaworks.algafoodapi.api.v1.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.hateoas.CollectionModel;

import com.algaworks.algafoodapi.api.v1.model.UsuarioModel;
import com.algaworks.algafoodapi.api.v1.model.input.SenhaInput;
import com.algaworks.algafoodapi.api.v1.model.input.UsuarioComSenhaInput;
import com.algaworks.algafoodapi.api.v1.model.input.UsuarioInput;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Usuários")
public interface UsuarioControllerOpenApi {
	

    @Operation(summary = "Lista os usuários")
    CollectionModel<UsuarioModel> getAll();

    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "ID do usuário inválido"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @Operation(summary = "Busca um usuário por ID")
    UsuarioModel getById(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            Long usuarioId);

    @Operation(summary = "Cadastra um usuário")
    UsuarioModel create(
            @Parameter(name = "corpo", description = "Representação de um novo usuário", required = true)
            UsuarioComSenhaInput usuarioInput);
    
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @Operation(summary = "Atualiza um usuário por ID")
    UsuarioModel update(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            Long usuarioId,
            
            @Parameter(name = "corpo", description = "Representação de um usuário com os novos dados", required = true)
            UsuarioInput usuarioInput);

    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @Operation(summary = "Atualiza a senha de um usuário")
    void updateSenha(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            Long usuarioId,
            @Parameter(name = "corpo", description = "Representação de uma nova senha", required = true)
            SenhaInput senha);
}
