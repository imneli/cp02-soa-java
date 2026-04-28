package com.fiap.cp02.repository;

import com.fiap.cp02.entity.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Page<Sala> findAll(Pageable pageable);
    List<Sala> findByCapacidadeGreaterThanEqual(Integer capacidade);
    List<Sala> findByLocalizacaoContainingIgnoreCase(String localizacao);
}
