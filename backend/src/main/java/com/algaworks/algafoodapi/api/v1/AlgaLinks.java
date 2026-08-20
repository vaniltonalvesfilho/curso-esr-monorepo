package com.algaworks.algafoodapi.api.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.TemplateVariable.VariableType;

import com.algaworks.algafoodapi.api.v1.controller.CidadeController;
import com.algaworks.algafoodapi.api.v1.controller.CozinhaController;
import com.algaworks.algafoodapi.api.v1.controller.EstadoController;
import com.algaworks.algafoodapi.api.v1.controller.EstatisticasController;
import com.algaworks.algafoodapi.api.v1.controller.FluxoPedidoController;
import com.algaworks.algafoodapi.api.v1.controller.FormaPagamentoController;
import com.algaworks.algafoodapi.api.v1.controller.GrupoController;
import com.algaworks.algafoodapi.api.v1.controller.GrupoPermissaoController;
import com.algaworks.algafoodapi.api.v1.controller.PedidoController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteFormaPagamentoController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteProdutoController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteProdutoFotoController;
import com.algaworks.algafoodapi.api.v1.controller.RestauranteUsuarioResponsavelController;
import com.algaworks.algafoodapi.api.v1.controller.UsuarioController;
import com.algaworks.algafoodapi.api.v1.controller.UsuarioGrupoController;

@Component
public class AlgaLinks  {
	
	private static final TemplateVariables PAGINACAO_VARIABLES = new TemplateVariables(
    		new TemplateVariable("page", VariableType.REQUEST_PARAM),
    		new TemplateVariable("size", VariableType.REQUEST_PARAM),
    		new TemplateVariable("sort", VariableType.REQUEST_PARAM)
    );
	
	private static final TemplateVariables PROJECAO_VARIABLES = new TemplateVariables(
			new TemplateVariable("projecao", VariableType.REQUEST_PARAM));
	
	public Link linkToPedidos(String rel) {
	         
        TemplateVariables filterVariables = new TemplateVariables(
        		new TemplateVariable("clienteId", VariableType.REQUEST_PARAM),
        		new TemplateVariable("restauranteId", VariableType.REQUEST_PARAM),
        		new TemplateVariable("dataCriacaoInicio", VariableType.REQUEST_PARAM),
        		new TemplateVariable("dataCriacaoFim", VariableType.REQUEST_PARAM)
        );
         
        String pedidosUrl = linkTo(PedidoController.class).toUri().toString();
        return Link.of(
        		UriTemplate.of(pedidosUrl, PAGINACAO_VARIABLES.concat(filterVariables)), 
        		rel);
	}
	
	public Link linkToEstatisticasVendasDiarias(String rel) {
		TemplateVariables filtroVariables = new TemplateVariables(
				new TemplateVariable("restauranteId", VariableType.REQUEST_PARAM),
				new TemplateVariable("dataCriacaoInicio", VariableType.REQUEST_PARAM),
				new TemplateVariable("dataCriacaoFim", VariableType.REQUEST_PARAM),
				new TemplateVariable("timeOffset", VariableType.REQUEST_PARAM)
		);
		
		String pedidosUrl = linkTo(methodOn(EstatisticasController.class)
				.consultarVendasDiarias(null, null)).toUri().toString();
		return Link.of(UriTemplate.of(pedidosUrl, filtroVariables), rel);
	}
	
	public Link linkToEstatiscas(String rel) {
		return linkTo(EstatisticasController.class).withRel(rel);
	}
	
	public Link linkToProdutos(Long restauranteId, String rel) {
		return linkTo(methodOn(RestauranteProdutoController.class)
				.getAll(restauranteId, null)).withRel(rel);
	}
	
	public Link linkToProdutos(Long restauranteId) {
		return linkToProdutos(restauranteId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToFotoProduto(Long restauranteId, Long produtoId, String rel) {
		return linkTo(
				methodOn(RestauranteProdutoFotoController.class)
					.buscar(restauranteId, produtoId)
		).withRel(rel);
	}
	
	public Link linkToProduto(Long restauranteId, Long produtoId, String rel) {
		return linkTo(methodOn(RestauranteProdutoController.class)
				.getById(restauranteId, produtoId))
				.withRel(rel);
	}
	
	public Link linkToProduto(Long restauranteId, Long produtoId) {
		return linkToProduto(restauranteId, produtoId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToFotoProduto(Long restauranteId, Long produtoId) {
		return linkToFotoProduto(restauranteId, produtoId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToRestaurantes(String rel) {
		String restauratesUrl = linkTo(RestauranteController.class).toUri().toString();
		return Link.of(
				UriTemplate.of(restauratesUrl, PROJECAO_VARIABLES), rel);
	}
	
	public Link linkToRestaurante(Long restauranteId) {
		return linkToRestaurante(restauranteId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToRestaurante(Long restauranteId, String rel) {
		return linkTo(methodOn(RestauranteController.class)
	             .buscar(restauranteId)).withRel(rel);
	}
	

	public Link linkToRestaurantes() {
	    return linkToRestaurantes(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToRestauranteFormasPagamento(Long restauranteId, String rel) {
	    return linkTo(methodOn(RestauranteFormaPagamentoController.class)
	            .listar(restauranteId)).withRel(rel);
	}
	
	public Link linkToRestauranteFormasPagamento(Long restauranteId) {
	    return linkTo(methodOn(RestauranteFormaPagamentoController.class)
	            .listar(restauranteId)).withRel(IanaLinkRelations.COLLECTION.value());
	}

	public Link linkToCozinha(Long cozinhaId, String rel) {
	    return linkTo(methodOn(CozinhaController.class)
	            .getById(cozinhaId)).withRel(rel);
	}

	public Link linkToCozinha(Long cozinhaId) {
	    return linkToCozinha(cozinhaId, IanaLinkRelations.SELF.value());
	}     
	
	public Link linkToCliente(Long clienteId) {
		return linkToCliente(clienteId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToCliente(Long clienteId, String rel) {
		return linkTo(methodOn(UsuarioController.class)
	             .getById(clienteId)).withRel(rel);
	}
	
	public Link linkToFormaPagamento(Long formaPagamentoId) {
		return linkToFormaPagamento(formaPagamentoId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToFormaPagamento(Long formaPagamentoId, String rel) {
		return linkTo(methodOn(FormaPagamentoController.class)
                .getById(formaPagamentoId, null)).withRel(rel);
	}
	
	public Link linkToFormasPagamento(String rel) {
		return linkTo(methodOn(FormaPagamentoController.class).getAll(null)).withRel(rel);
	}
	
	public Link linkToFormasPagamento() {
		return linkTo(methodOn(FormaPagamentoController.class).getAll(null)).withRel(IanaLinkRelations.COLLECTION);
	} 
	
	public Link linkToRestauranteFormaPagamentoDesassociacao(
			Long restauranteId, Long formaPagamentoId, String rel) {
		
		return linkTo(methodOn(RestauranteFormaPagamentoController.class)
				.desassociar(restauranteId, formaPagamentoId)).withRel(rel);
	}
	
	public Link linkToRestauranteFormaPagamentoAssociacao(
			Long restauranteId, String rel) {
		
		return linkTo(methodOn(RestauranteFormaPagamentoController.class)
				.associar(restauranteId, null)).withRel(rel);
	}
	
	public Link linkToCidade(Long id, String rel) {
		return linkTo(methodOn(CidadeController.class).getById(id)).withRel(rel);
	}
	
	public Link linkToCidade(Long id) {
		return linkToCidade(id, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToEnderecoEntrega(Long cidadeId) {
		return linkToEnderecoEntrega(cidadeId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToEnderecoEntrega(Long cidadeId, String rel) {
		return linkTo(methodOn(CidadeController.class)
                .getById(cidadeId)).withRel(rel);
	}
	
	public Link linkToItens(Long restauranteId, Long produtoId) {
		return linkToItens(restauranteId, produtoId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToItens(Long restauranteId, Long produtoId, String rel) {
		return linkTo(methodOn(RestauranteProdutoController.class)
                .getById(restauranteId, produtoId))
                .withRel(rel);
	}
	
	public Link linkToCidades(String rel) {
		return linkTo(methodOn(CidadeController.class).getAll()).withRel(rel);
	}
	
	public Link linkToCidades() {
		return linkToCidades(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToEstados(String rel) {
		return linkTo(methodOn(EstadoController.class).getAll()).withRel(rel);
	}
	
	public Link linkToEstados() {
		return linkToEstados(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToCozinhas(String rel) {
		return linkTo(CozinhaController.class).withRel(rel);
	}
	
	public Link linkToCozinhas() {
		return linkToCozinhas(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToUsuarios(String rel) {
		return linkTo(methodOn(UsuarioController.class).getAll()).withRel(rel);
	}
	
	public Link linkToUsuarios() {
		return linkToUsuarios(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToGrupoUsuarios(Long usuarioId, String rel) {
		return linkTo(methodOn(UsuarioGrupoController.class)
				.getAll(usuarioId)).withRel(rel);
	}
	
	public Link linkToGrupoUsuarios(Long usuarioId) {
		return linkToGrupoUsuarios(usuarioId, IanaLinkRelations.SELF.value());
	}
	
	
	public Link linkToPermissoes(String rel) {
		return linkTo(GrupoPermissaoController.class).withRel(rel);
	}
	
	public Link linkToPermissoes() {
		return linkToPermissoes(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToGrupos(String rel) {
		return linkTo(methodOn(GrupoController.class).getAll()).withRel(rel);
	}
	
	public Link linkToGrupos() {
		return linkTo(methodOn(GrupoController.class).getAll()).withRel(IanaLinkRelations.SELF.value());
	}
	
	
	public Link linkToGrupo(Long grupoId, String rel) {
		return linkTo(methodOn(GrupoController.class).getById(grupoId)).withRel(rel);
	}
	
	public Link linkToGrupo(Long grupoId) {
		return linkToGrupo(grupoId, IanaLinkRelations.SELF.value());
	}
	
	public Link linkToGrupoPermissao(Long grupoId, String rel) {
		return linkTo(
				methodOn(GrupoPermissaoController.class)
					.getAll(grupoId)
				).withRel(rel);
	}
	
	public Link linkUsuarioToGrupoAssociacao(Long usuarioId, String rel) {
		return linkTo(
				methodOn(UsuarioGrupoController.class)
					.associar(usuarioId, null)
				).withRel(rel);
	}
	
	public Link linkUsuarioToGrupoDesassociacao(Long usuarioId, Long grupoId, String rel) {
		return linkTo(
				methodOn(UsuarioGrupoController.class)
					.desassociar(usuarioId, grupoId)
				).withRel(rel);
	}
	
	public Link linkToResponsaveisRestaurantes(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteUsuarioResponsavelController.class)
				.getAll(restauranteId)).withRel(rel);
	}
	
	public Link linkToResponsaveisRestaurantes(Long restauranteId) {
		return linkTo(
				methodOn(RestauranteUsuarioResponsavelController.class)
				.getAll(restauranteId)).withRel(IanaLinkRelations.SELF.value());
	}
	
	public Link linkToResponsaveisRestaurantesDesassociacao(Long restauranteId, Long usuarioId, String rel) {
		return linkTo(
				methodOn(RestauranteUsuarioResponsavelController.class)
					.desassociarResponsavel(restauranteId, usuarioId)
				).withRel(rel);
	}
	
	public Link linkToResponsaveisRestaurantesAssociacao(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteUsuarioResponsavelController.class)
					.associarResponsavel(restauranteId, null)
				).withRel(rel);
	}
	
	
	public Link linkToConfirmacaoPedido(String codigo, String rel) {
		return linkTo(
				methodOn(FluxoPedidoController.class)
					.confirmar(codigo))
				.withRel(rel);
	}
	
	public Link linkToCancelamentoPedido(String codigo, String rel) {
		return linkTo(
				methodOn(FluxoPedidoController.class)
					.cancelar(codigo))
				.withRel(rel);
	}
	
	public Link linkToEntregaPedido(String codigo, String rel) {
		return linkTo(
				methodOn(FluxoPedidoController.class)
					.entregar(codigo))
				.withRel(rel);
	}
	
	public Link linkToAbrirRestaurante(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteController.class)
					.abertura(restauranteId))
				.withRel(rel);
	}
	
	public Link linkToFecharRestaurante(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteController.class)
					.fechamento(restauranteId))
				.withRel(rel);
	}
	
	public Link linkToAtivarRestaurante(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteController.class)
					.ativar(restauranteId))
				.withRel(rel);
	}
	
	public Link linkToInativarRestaurante(Long restauranteId, String rel) {
		return linkTo(
				methodOn(RestauranteController.class)
					.inativar(restauranteId))
				.withRel(rel);
	}
	

}
