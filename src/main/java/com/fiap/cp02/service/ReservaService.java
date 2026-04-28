package com.fiap.cp02.service;

import com.fiap.cp02.dto.ReservaDTO;
import com.fiap.cp02.entity.Reserva;
import com.fiap.cp02.entity.Sala;
import com.fiap.cp02.exception.ReservaConflictException;
import com.fiap.cp02.exception.ResourceNotFoundException;
import com.fiap.cp02.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final SalaService salaService;

    public ReservaService(ReservaRepository reservaRepository, SalaService salaService) {
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
    }

    public ReservaDTO criar(ReservaDTO dto) {
        log.info("Creating reservation for room {} by {}", dto.getSalaId(), dto.getResponsavel());
        validarIntervalo(dto.getDataHoraInicio(), dto.getDataHoraFim());
        Sala sala = salaService.findById(dto.getSalaId());
        verificarConflito(dto.getSalaId(), dto.getDataHoraInicio(), dto.getDataHoraFim(), -1L);

        Reserva reserva = new Reserva();
        reserva.setSala(sala);
        reserva.setDataHoraInicio(dto.getDataHoraInicio());
        reserva.setDataHoraFim(dto.getDataHoraFim());
        reserva.setResponsavel(dto.getResponsavel());

        return toDTO(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO> listar(Pageable pageable) {
        log.debug("Listing reservations, page: {}", pageable.getPageNumber());
        return reservaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO> listarPorSala(Long salaId, Pageable pageable) {
        salaService.findById(salaId);
        return reservaRepository.findBySalaId(salaId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO> filtrar(Long salaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        log.debug("Filtering reservations - salaId={}, inicio={}, fim={}", salaId, inicio, fim);
        return reservaRepository.findWithFilters(salaId, inicio, fim, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ReservaDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    public void cancelar(Long id) {
        log.info("Cancelling reservation id: {}", id);
        findById(id);
        reservaRepository.deleteById(id);
    }

    void verificarConflito(Long salaId, LocalDateTime inicio, LocalDateTime fim, Long excludeId) {
        if (reservaRepository.existsConflict(salaId, inicio, fim, excludeId)) {
            throw new ReservaConflictException(
                    "Conflito de reserva: a sala já está ocupada no intervalo solicitado");
        }
    }

    void validarIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("Data/hora de fim deve ser posterior ao início");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data/hora de início deve ser futura");
        }
        if (java.time.Duration.between(inicio, fim).toMinutes() < 15) {
            throw new IllegalArgumentException("A reserva deve ter duração mínima de 15 minutos");
        }
    }

    private Reserva findById(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada: " + id));
    }

    private ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getId(),
                reserva.getSala().getId(),
                reserva.getSala().getNome(),
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getResponsavel()
        );
    }
}
