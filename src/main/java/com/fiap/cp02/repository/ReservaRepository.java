package com.fiap.cp02.repository;

import com.fiap.cp02.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findAll(Pageable pageable);

    List<Reserva> findBySalaId(Long salaId);

    Page<Reserva> findBySalaId(Long salaId, Pageable pageable);

    @Query("""
        SELECT COUNT(r) > 0 FROM Reserva r
        WHERE r.sala.id = :salaId
          AND r.id <> :excludeId
          AND r.dataHoraInicio < :fim
          AND r.dataHoraFim > :inicio
    """)
    boolean existsConflict(
        @Param("salaId") Long salaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        @Param("excludeId") Long excludeId
    );

    List<Reserva> findByResponsavelContainingIgnoreCase(String responsavel);

    @Query("""
        SELECT r FROM Reserva r
        WHERE (:salaId IS NULL OR r.sala.id = :salaId)
          AND (:inicio IS NULL OR r.dataHoraInicio >= :inicio)
          AND (:fim IS NULL OR r.dataHoraFim <= :fim)
    """)
    Page<Reserva> findWithFilters(
        @Param("salaId") Long salaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        Pageable pageable
    );
}
