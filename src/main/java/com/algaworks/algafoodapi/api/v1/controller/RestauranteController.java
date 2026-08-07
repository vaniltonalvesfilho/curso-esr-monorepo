package com.algaworks.algafoodapi.api.v1.controller;

import java.util.List;

import com.algaworks.algafoodapi.api.v1.convert.RestauranteApenasNomeConvert;
import com.algaworks.algafoodapi.api.v1.convert.RestauranteBasicConvert;
import com.algaworks.algafoodapi.api.v1.convert.RestauranteConverter;
import com.algaworks.algafoodapi.api.v1.convert.RestauranteInputConverter;
import com.algaworks.algafoodapi.api.v1.model.RestauranteApenasNomeModel;
import com.algaworks.algafoodapi.api.v1.model.RestauranteBasicModel;
import com.algaworks.algafoodapi.api.v1.model.RestauranteModel;
import com.algaworks.algafoodapi.api.v1.model.input.RestauranteInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.RestauranteControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.exception.CidadeNaoEncontradaException;
import com.algaworks.algafoodapi.domain.exception.CozinhaNaoEncontradaException;
import com.algaworks.algafoodapi.domain.exception.NegocioException;
import com.algaworks.algafoodapi.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import com.algaworks.algafoodapi.domain.repository.RestauranteRepository;
import com.algaworks.algafoodapi.domain.service.CadastroRestauranteService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v1/restaurantes", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestauranteController implements RestauranteControllerOpenApi {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CadastroRestauranteService restauranteService;

    @Autowired
    private RestauranteConverter convert;

    @Autowired
    private RestauranteInputConverter inputConvert;
    
    @Autowired
    private RestauranteBasicConvert restauranteBasicoModelAssembler;

    @Autowired
    private RestauranteApenasNomeConvert restauranteApenasNomeModelAssembler; 

//    @JsonView(RestauranteViewModel.Resumo.class)
    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping
    @Override
    public CollectionModel<RestauranteBasicModel> listar() {
        return restauranteBasicoModelAssembler
        		.toCollectionModel(restauranteRepository.findAll());
       
    }

   
//    @JsonView(RestauranteViewModel.ApenasNome.class)
    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping(params = {"projecao=apenas-nome"})
    @Override
    public CollectionModel<RestauranteApenasNomeModel> listarApenasNome() {
        return restauranteApenasNomeModelAssembler
        			.toCollectionModel(restauranteRepository.findAll());
    }

    @CheckSecurity.Restaurantes.PodeConsultarRestaurantes
    @GetMapping("/{restauranteId}")
    @Override
    public RestauranteModel buscar(@PathVariable Long restauranteId) {
        Restaurante restaurante = restauranteService.buscarOuFalhar(restauranteId);

        return convert.toModel(restaurante);
    }


    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public RestauranteModel adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
        try {
            Restaurante restaurante = inputConvert.toDomainObject(restauranteInput);
            return convert.toModel(restauranteService.salvar(restaurante));
        } catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @PutMapping("/{restauranteId}")
    @Override
    public RestauranteModel atualizar(@PathVariable Long restauranteId, @RequestBody @Valid RestauranteInput restauranteInput) {
        try {
        	
//            Restaurante restaurante = inputConvert.toDomainObject(restauranteInput);
            var restauranteAtual = restauranteService.buscarOuFalhar(restauranteId);
            inputConvert.copyToDomainObject(restauranteInput, restauranteAtual);
//            BeanUtils.copyProperties(
//                    restaurante, restauranteAtual,
//                    "id", "formaPagamentos", "endereco", "dataCadastro");
            return convert.toModel(restauranteService.salvar(restauranteAtual));
            
        } catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e ) {
        	
            throw new NegocioException(e.getMessage());
            
        }

    }

//    @PatchMapping("/{restauranteId}")
//    public RestauranteModel atualizarParcial(@PathVariable Long restauranteId,
//                                        @RequestBody Map<String, Object> restaurante, HttpServletRequest request) {
//
//        var restauranteAtual = restauranteService.buscarOuFalhar(restauranteId);
//        restauranteService.merge(restaurante, restauranteAtual, request);
//        validade(restauranteAtual, "restaurante");
//
//        return atualizar(restauranteId, restauranteAtual);
//    }
//
//    private void validade(Restaurante restaurante, String objectName) {
//        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objectName);
//        validator.validate(restaurante, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            throw new ValidacaoException(bindingResult);
//        }
//    }

    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @DeleteMapping("/{restauranteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void remover(@PathVariable Long restauranteId) {
        restauranteService.excluir(restauranteId);
    }


    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @PutMapping("/{restauranteId}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> ativar(@PathVariable Long restauranteId) {
        restauranteService.ativar(restauranteId);
        
        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @DeleteMapping("/{restauranteId}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> inativar(@PathVariable Long restauranteId) {
        restauranteService.inativar(restauranteId);
        
        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @PutMapping("/{restauranteId}/abertura")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> abertura(@PathVariable Long restauranteId) {
        restauranteService.abrir(restauranteId);
        
        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @PutMapping("/{restauranteId}/fechamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> fechamento(@PathVariable Long restauranteId) {
        restauranteService.fechar(restauranteId);
        
        return ResponseEntity.noContent().build();
    }

    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @PutMapping("/ativacoes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void ativarMultiplos(@RequestBody List<Long> restauranteIds) {
        try {
            restauranteService.ativar(restauranteIds);
        } catch (RestauranteNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @CheckSecurity.Restaurantes.PodeGerenciarCadastro
    @DeleteMapping("/ativacoes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void inativarMultiplos(@RequestBody List<Long> restauranteIds) {
        try {
            restauranteService.inativar(restauranteIds);
        } catch (RestauranteNaoEncontradoException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }
	
}
