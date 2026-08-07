package com.algaworks.algafoodapi.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.hateoas.Links;

import com.algaworks.algafoodapi.api.v1.model.PedidoResumoModel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PedidosResumoModel")
@Getter
@Setter
public class PedidosResumoModelOpenApi {
    
    private PedidosResumoEmbeddedModelOpenApi _embedded;
    private Links _links;
    private PageModelOpenApi page;
    
    @Schema(name = "PedidosResumoEmbeddedModel")
    @Data
    public class PedidosResumoEmbeddedModelOpenApi {
        
        private List<PedidoResumoModel> pedidos;
        
    }   
}     
