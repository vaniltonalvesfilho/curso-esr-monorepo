package com.algaworks.algafoodapi.core.datautils;


import com.algaworks.algafoodapi.domain.exception.NegocioException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.stream.Collectors;

public class PageableTranslator {

    public static Pageable translate(Pageable pageable, Map<String, String> fieldsMapping) {
        var pedidos = pageable.getSort().stream()
                .filter(pedido -> {

                if (!fieldsMapping.containsKey(pedido.getProperty())) {
                    throw new NegocioException(
                            String.format("Propriedade passada por parâmetro não existe.")
                    );
                }

                    return true;
                })
                .map(pedido -> new Sort.Order(pedido.getDirection(), fieldsMapping.get(pedido.getProperty())))
                .collect(Collectors.toList());

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(pedidos));
    }
}
