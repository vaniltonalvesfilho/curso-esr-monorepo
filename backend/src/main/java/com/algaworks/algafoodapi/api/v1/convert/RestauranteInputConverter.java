package com.algaworks.algafoodapi.api.v1.convert;

import com.algaworks.algafoodapi.api.v1.model.input.RestauranteInput;
import com.algaworks.algafoodapi.domain.model.Cidade;
import com.algaworks.algafoodapi.domain.model.Cozinha;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class RestauranteInputConverter {

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager manager;

    public Restaurante toDomainObject(RestauranteInput restauranteInput) {
        return modelMapper.map(restauranteInput, Restaurante.class);
    }

    public void copyToDomainObject(RestauranteInput restauranteInput, Restaurante restaurante) {
//        manager.detach(restaurante.getCozinha());
//        if (restaurante.getEndereco()!= null)
//            manager.detach(restaurante.getEndereco().getCidade());

        restaurante.setCozinha(new Cozinha());
        if (restaurante.getEndereco() != null)
            restaurante.getEndereco().setCidade(new Cidade());
        modelMapper.map(restauranteInput, restaurante);
    }

}
