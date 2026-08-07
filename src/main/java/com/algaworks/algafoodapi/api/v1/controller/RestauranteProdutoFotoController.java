package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.convert.FotoProdutoConverter;
import com.algaworks.algafoodapi.api.v1.model.FotoProdutoModel;
import com.algaworks.algafoodapi.api.v1.model.input.FotoProdutoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.RestauranteProdFotoControllerOpenApi;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafoodapi.domain.model.FotoProduto;
import com.algaworks.algafoodapi.domain.model.Produto;
import com.algaworks.algafoodapi.domain.service.CatagoloFotoProdutoService;
import com.algaworks.algafoodapi.domain.service.FotoStorageService;
import com.algaworks.algafoodapi.domain.service.FotoStorageService.FotoRecuperada;
import com.algaworks.algafoodapi.domain.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/restaurantes/{restauranteId}/produtos/{produtoId}/foto", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestauranteProdutoFotoController implements RestauranteProdFotoControllerOpenApi {

    @Autowired
    private CatagoloFotoProdutoService catagoloFotoProdutoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private FotoStorageService fotoStorageService;

    @Autowired
    private FotoProdutoConverter fotoProdutoConverter;

	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
    @Override
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FotoProdutoModel atualizarFoto(@PathVariable Long restauranteId,
			@PathVariable Long produtoId, @Valid FotoProdutoInput fotoProdutoInput,
			@RequestPart(required = true) MultipartFile arquivo) throws IOException {
		Produto produto = produtoService.findOrFail(restauranteId, produtoId);

		FotoProduto foto = new FotoProduto();
		foto.setProduto(produto);
		foto.setDescricao(fotoProdutoInput.getDescricao());
		foto.setContentType(arquivo.getContentType());
		foto.setTamanho(arquivo.getSize());
		foto.setNomeArquivo(arquivo.getOriginalFilename());
		
		FotoProduto fotoSalva = catagoloFotoProdutoService.salvar(foto, arquivo.getInputStream());
		
		return fotoProdutoConverter.toModel(fotoSalva);
	}

	@CheckSecurity.Restaurantes.PodeGerenciarFuncionamento
	@Override
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long restauranteId, 
			@PathVariable Long produtoId) {
		catagoloFotoProdutoService.delete(restauranteId, produtoId);
	}

	@CheckSecurity.Restaurantes.PodeConsultarRestaurantes
	@Override
	@GetMapping
	public FotoProdutoModel buscar(@PathVariable Long restauranteId, 
			@PathVariable Long produtoId) {
		FotoProduto fotoProduto = catagoloFotoProdutoService.buscarOuFalhar(restauranteId, produtoId);
		
		return fotoProdutoConverter.toModel(fotoProduto);
	}
	
	@Override
	@GetMapping(produces = MediaType.ALL_VALUE)
	public ResponseEntity<?> servir(@PathVariable Long restauranteId, 
			@PathVariable Long produtoId, @RequestHeader(name = "accept") String acceptHeader) 
					throws HttpMediaTypeNotAcceptableException {
		try {
			FotoProduto fotoProduto = catagoloFotoProdutoService.buscarOuFalhar(restauranteId, produtoId);
			
			MediaType mediaTypeFoto = MediaType.parseMediaType(fotoProduto.getContentType());
			List<MediaType> mediaTypesAceitas = MediaType.parseMediaTypes(acceptHeader);
			
			verificarCompatibilidadeMediaType(mediaTypeFoto, mediaTypesAceitas);
			
			FotoRecuperada fotoRecuperada = fotoStorageService.recuperar(fotoProduto.getNomeArquivo());
			
			if (fotoRecuperada.temUrl()) {
				return ResponseEntity
						.status(HttpStatus.FOUND)
						.header(HttpHeaders.LOCATION, fotoRecuperada.getUrl())
						.contentLength(fotoProduto.getTamanho())
						.contentType(mediaTypeFoto)
						.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
						.build();
			} else {
				return ResponseEntity.ok()
						.contentLength(fotoProduto.getTamanho())
						.contentType(mediaTypeFoto)
						.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
						.body(new InputStreamResource(fotoRecuperada.getInputStream()));
			}
		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.notFound().build();
		}
	}

	private void verificarCompatibilidadeMediaType(MediaType mediaTypeFoto, 
			List<MediaType> mediaTypesAceitas) throws HttpMediaTypeNotAcceptableException {
		
		boolean compativel = mediaTypesAceitas.stream()
				.anyMatch(mediaTypeAceita -> mediaTypeAceita.isCompatibleWith(mediaTypeFoto));
		
		if (!compativel) {
			throw new HttpMediaTypeNotAcceptableException(mediaTypesAceitas);
		}
	}
	
	
	
}
