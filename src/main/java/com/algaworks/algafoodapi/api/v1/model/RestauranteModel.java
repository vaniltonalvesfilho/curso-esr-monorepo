package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

//import com.algaworks.algafoodapi.api.model.view.RestauranteViewModel;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation("restaurantes")
@Setter
@Getter
public class RestauranteModel extends RepresentationModel<RestauranteModel> {

	@Schema(example = "1")
//    @JsonView({RestauranteViewModel.Resumo.class, RestauranteViewModel.ApenasNome.class})
    private Long id;

	@Schema(example = "Thai Gourmet")
//    @JsonView({RestauranteViewModel.Resumo.class, RestauranteViewModel.ApenasNome.class})
    private String nome;

	@Schema(example = "10.50")
//    @JsonView(RestauranteViewModel.Resumo.class)
    private BigDecimal taxaFrete;

//    @JsonView(RestauranteViewModel.Resumo.class)
    private CozinhaModel cozinha;

    @Schema(example = "true")
    private Boolean ativo;
    
    @Schema(example = "true")
    private Boolean aberto;
    
    private EnderecoModel endereco;
}
