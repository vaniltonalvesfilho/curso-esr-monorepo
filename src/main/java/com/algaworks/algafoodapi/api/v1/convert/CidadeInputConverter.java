package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.model.input.CidadeInput;
import com.algaworks.algafoodapi.domain.model.Cidade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Component
public class CidadeInputConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityManager manager;

    public Cidade toDomainObject(CidadeInput cidadeInput) {
        return modelMapper.map(cidadeInput, Cidade.class);
    }

    public void copyToDomainObject(CidadeInput cidadeInput, Cidade cidade) {
        manager.detach(cidade.getEstado());

        modelMapper.map(cidadeInput, cidade);
    }
}

