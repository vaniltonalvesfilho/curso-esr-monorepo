package com.algaworks.algafoodapi.api.v1.convert;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class Converter {

    @Autowired
    private CidadeConverter cidadeConverter;

    @Autowired
    private CidadeInputConverter cidadeInputConverter;

}
