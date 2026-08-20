package com.algaworks.algafoodapi.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import com.algaworks.algafoodapi.domain.model.Restaurante;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteRepository extends CustomJpaRepository<Restaurante, Long>, RestauranteRepositoryQueries, JpaSpecificationExecutor<Restaurante> {

	@Query("from Restaurante r join fetch r.cozinha")
	List<Restaurante> findAll();
	
    List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
    // @Query("from Restaurante where nome like %:nome% and cozinha.id = :id")
    List<Restaurante> consultarPorNome(String nome, @Param("id") Long cozinha);
    // List<Restaurante> findByNomeContainingAndCozinhaId(String nome, Long cozinha);
    int countByCozinhaId(Long cozinha);

    boolean existsResponsavel(Long restauranteId, Long usuarioId);
}
