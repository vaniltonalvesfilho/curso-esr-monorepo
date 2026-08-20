package com.algaworks.algafoodapi.domain.service;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.util.UUID;

public interface FotoStorageService {

    FotoRecuperada recuperar(String nomeArquivo);

    void armazenar(NovaFoto novaFoto);

    void remover(String nomeArquivo);

    default void substituir(String nomeArquivoAntigo, NovaFoto novaFoto) {
        this.armazenar(novaFoto);

        if (nomeArquivoAntigo != null) {
            this.remover(nomeArquivoAntigo);
        }
    }

    default String gerarNomeArquivo(String nomeOriginal) {
        return UUID.randomUUID() + "_" + nomeOriginal;
    }

    @Getter
    @Builder
    public class NovaFoto {
        private String nomeArquivo;
        String contentType;
        private InputStream inputStream;
    }

    @Builder
    @Getter
    public class FotoRecuperada {
        private InputStream inputStream;
        private String url;

        public boolean temInputStream() {
            return inputStream != null;
        }

        public boolean temUrl() {
            return url != null;
        }

    }

}
