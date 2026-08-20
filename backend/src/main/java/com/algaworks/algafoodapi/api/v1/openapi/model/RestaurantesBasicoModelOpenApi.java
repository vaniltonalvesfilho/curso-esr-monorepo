package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v1.model.RestauranteBasicModel;
import com.algaworks.algafoodapi.api.v1.model.RestauranteResumoModel;

import lombok.Data;

@Schema(name = "RestaurantesBasicoModel")
@Data
public class RestaurantesBasicoModelOpenApi {

    private RestaurantesEmbeddedModelOpenApi _embedded;
    private Links _links;
    
    @Schema(name = "RestaurantesEmbeddedModel")
    @Data
    public class RestaurantesEmbeddedModelOpenApi {
        
        private List<RestauranteBasicModel> restaurantes;
        
    }
    
}        