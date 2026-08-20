package com.algaworks.algafoodapi.api.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafoodapi.api.v1.convert.PermissaoConvert;
import com.algaworks.algafoodapi.api.v1.model.PermissaoModel;
import com.algaworks.algafoodapi.api.v1.openapi.controller.PermissaoControllerOpenApi;
import com.algaworks.algafoodapi.domain.model.Permissao;
import com.algaworks.algafoodapi.domain.repository.PermissaoRepository;

@RestController
@RequestMapping(path = "/v1/permissoes", produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissaoController implements PermissaoControllerOpenApi {
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	
	@Autowired
	private PermissaoConvert permissaoConvert;
	
	@Override
	@GetMapping
	public CollectionModel<PermissaoModel> listar() {
		List<Permissao> permissoes = permissaoRepository.findAll();
		return permissaoConvert.toCollectionModel(permissoes);
	}
	
}
