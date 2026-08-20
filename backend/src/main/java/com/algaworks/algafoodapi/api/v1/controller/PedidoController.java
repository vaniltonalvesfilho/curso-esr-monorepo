package com.algaworks.algafoodapi.api.v1.controller;

import com.algaworks.algafoodapi.api.v1.convert.PedidoConvert;
import com.algaworks.algafoodapi.api.v1.convert.PedidoInputConvert;
import com.algaworks.algafoodapi.api.v1.convert.PedidoResumoConvert;
import com.algaworks.algafoodapi.api.v1.model.PedidoModel;
import com.algaworks.algafoodapi.api.v1.model.PedidoResumoModel;
import com.algaworks.algafoodapi.api.v1.model.input.PedidoInput;
import com.algaworks.algafoodapi.api.v1.openapi.controller.PedidoControllerOpenApi;
import com.algaworks.algafoodapi.core.datautils.PageWrapper;
import com.algaworks.algafoodapi.core.datautils.PageableTranslator;
import com.algaworks.algafoodapi.core.security.AlgaSecurity;
import com.algaworks.algafoodapi.core.security.CheckSecurity;
import com.algaworks.algafoodapi.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafoodapi.domain.exception.NegocioException;
import com.algaworks.algafoodapi.domain.model.Pedido;
import com.algaworks.algafoodapi.domain.model.Usuario;
import com.algaworks.algafoodapi.domain.repository.PedidoRepository;
import com.algaworks.algafoodapi.domain.filter.PedidoFilter;
import com.algaworks.algafoodapi.domain.service.EmissaoPedidoService;
import com.algaworks.algafoodapi.infrastructure.repository.spec.PedidoSpecs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/v1/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoController implements PedidoControllerOpenApi {


	@Autowired
	private PedidoRepository pedidoRepository;
	
    @Autowired
    private EmissaoPedidoService emissaoPedidoService;

    @Autowired
    private PedidoConvert pedidoConvert;

    @Autowired
    private PedidoResumoConvert pedidoResumoConvert;

    @Autowired
    private PedidoInputConvert pedidoInputConvert;
    
    @Autowired
    private PagedResourcesAssembler<Pedido> pagedResourceAssembler;

    @Autowired
    private AlgaSecurity algaSecurity;

    @CheckSecurity.Pedidos.PodeBuscar
    @GetMapping
    @Override
	public PagedModel<PedidoResumoModel> pesquisar(PedidoFilter filtro,
			@PageableDefault(size = 10) Pageable pageable) {
		Pageable pageableTraduzido = traduzirPageable(pageable);
		
		Page<Pedido> pedidosPage = pedidoRepository.findAll(
				PedidoSpecs.usandoFiltro(filtro), pageableTraduzido);
		
		pedidosPage = new PageWrapper<>(pedidosPage, pageable);
		
		return pagedResourceAssembler.toModel(pedidosPage, pedidoResumoConvert);
	}
//    @GetMapping
//    public MappingJacksonValue getAll(@RequestParam(required = false) String campos) {
//        List<Pedido> pedidos = emissaoPedidoService.getAll();
//        List<PedidoResumoModel> pedidosResumoModel = pedidoResumoConvert.toCollectionModel(pedidos);
//
//        MappingJacksonValue pedidosWrapper = new MappingJacksonValue(pedidosResumoModel);
//        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//        filterProvider.addFilter("pedidoFilter", SimpleBeanPropertyFilter.serializeAll());
//
//        if (StringUtils.isNotBlank(campos)) {
//            filterProvider.addFilter("pedidoFilter", SimpleBeanPropertyFilter.filterOutAllExcept(campos.split(",")));
//        }
//        pedidosWrapper.setFilters(filterProvider);
//
//        return pedidosWrapper;
//    }

    @CheckSecurity.Pedidos.PodeBuscar
    @GetMapping("/{codigo}")
    @Override
    public PedidoModel getById(@PathVariable String codigo) {
        Pedido pedido = emissaoPedidoService.findOrFail(codigo);
        return pedidoConvert.toModel(pedido);
    }

    @CheckSecurity.Pedidos.PodeCriar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoModel add(@RequestBody @Valid PedidoInput pedidoInput) {
        try {
            Pedido novoPedido = pedidoInputConvert.toDomainObject(pedidoInput);

            novoPedido.setCliente(new Usuario());
            novoPedido.getCliente().setId(algaSecurity.getUsuarioId());

            novoPedido = emissaoPedidoService.emitir(novoPedido);

            return pedidoConvert.toModel(novoPedido);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private Pageable traduzirPageable(Pageable pageable) {
        var mapping = Map.of(
                "codigo", "codigo",
                "nomerestaurante", "restaurante.nome",
                "nomeCliente", "cliente.nome",
                "valorTotal", "valorTotal"
        );

        return PageableTranslator.translate(pageable, mapping);
    }

}
