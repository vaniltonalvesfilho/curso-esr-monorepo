package com.algaworks.algafoodapi.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "Links")
public class LinksModelOpenApi {
	
	private LinkModel rel;
	
	@Setter
	@Getter
	@Schema(name = "Link")
	private class LinkModel {
		
		private String href;
		private boolean templated;
		
	}
	
}
