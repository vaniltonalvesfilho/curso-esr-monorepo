package com.algaworks.algafoodapi.domain.repository;

import java.util.List;
import java.util.Optional;

import com.algaworks.algafoodapi.domain.model.Cozinha;

import org.springframework.stereotype.Repository;


@Repository
public interface CozinhaRepository extends CustomJpaRepository<Cozinha, Long> {

    List<Cozinha> findQualquerCoisaSeiLaByNomeContaining(String nome);    
 
    Optional<Cozinha> findByNome(String nome);

    boolean existsByNome(String nome);
}
