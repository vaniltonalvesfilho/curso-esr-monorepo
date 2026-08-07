package com.algaworks.algafoodapi.api.v1.controller;


import com.algaworks.algafoodapi.api.v1.convert.CozinhaConverter;
import com.algaworks.algafoodapi.api.v1.convert.CozinhaInputConverter;
import com.algaworks.algafoodapi.api.v1.model.CozinhaModel;
import com.algaworks.algafoodapi.api.v1.model.input.CozinhaInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.CozinhaControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.model.Cozinha;
import com.algaworks.algafoodapi.domain.repository.CozinhaRepository;
import com.algaworks.algafoodapi.domain.service.CadastroCozinhaService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Slf4j
@RestController
@RequestMapping(path = "/v1/cozinhas", produces = MediaType.APPLICATION_JSON_VALUE)
public class CozinhaController implements CozinhaControllerOpenApi {
	
    @Autowired
    private CozinhaRepository cozinhaRepository;

    @Autowired
    private CadastroCozinhaService cadastroCozinhaService;

    @Autowired
    private CozinhaConverter cozinhaConverter;

    @Autowired
    private CozinhaInputConverter cozinhaInputConverter;

	@Autowired
	private PagedResourcesAssembler<Cozinha> pagedResourcesAssembler;
	
    /**
     * Obtém uma lista de cozinhas da base de dados.
     * 
     * @return A instância de uma lista de cozinhas.
     */
    @CheckSecurity.Cozinhas.PodeConsultarCozinhas
	@Override
	@GetMapping
	public PagedModel<CozinhaModel> getAll(@PageableDefault(size = 10) Pageable pageable) {
//		System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		Page<Cozinha> cozinhasPage = cozinhaRepository.findAll(pageable);
		
		// 2 conversões
		PagedModel<CozinhaModel> cozinhasPagedModel = pagedResourcesAssembler
				.toModel(cozinhasPage, cozinhaConverter);
		
		return cozinhasPagedModel;
	}

    /**
     * Obtém um singleton resource da base de dados.
     * 
     * @param cozinhaId O id do objeto a ser recuperado da base de dados.
     * @return A instância da cozinha buscada.
     */
    @CheckSecurity.Cozinhas.PodeConsultarCozinhas
    @GetMapping("/{cozinhaId}")
    public CozinhaModel getById(@PathVariable Long cozinhaId) {
       return cozinhaConverter.toModel(cadastroCozinhaService.buscarOuFalhar(cozinhaId));
    }

    @CheckSecurity.Cozinhas.PodeEditarCozinhas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CozinhaModel add(@RequestBody @Valid CozinhaInput cozinhaInput) {
        return cozinhaConverter.toModel(cadastroCozinhaService.save(cozinhaInputConverter.toDomainObject(cozinhaInput)));
    }

    @CheckSecurity.Cozinhas.PodeEditarCozinhas
    @PutMapping("/{cozinhaId}")
    public CozinhaModel set(@PathVariable Long cozinhaId, @RequestBody @Valid CozinhaInput cozinhaInput) {
        Cozinha cozinhaAtual = cadastroCozinhaService.buscarOuFalhar(cozinhaId);
        cozinhaInputConverter.copyToDomainObject(cozinhaInput, cozinhaAtual);
        return cozinhaConverter.toModel(cadastroCozinhaService.save(cozinhaAtual));
    }

    @CheckSecurity.Cozinhas.PodeEditarCozinhas
    @DeleteMapping("/{cozinhaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long cozinhaId) {
            cadastroCozinhaService.excluirPorId(cozinhaId);
    }
}
