package com.algaworks.algafoodapi.api.v1.model.input;

import io.swagger.v3.oas.annotations.media.Schema;

import com.algaworks.algafoodapi.core.validation.FileContentType;
import com.algaworks.algafoodapi.core.validation.FileSize;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class FotoProdutoInput {

	@Schema(hidden = true)
    @NotNull
    @FileSize(max = "500KB")
    @FileContentType(allowed = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    private MultipartFile arquivo;

	@Schema(description = "Descrição da foto do produto", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String descricao;

}
