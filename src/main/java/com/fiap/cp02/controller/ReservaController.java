package com.fiap.cp02.controller;

import com.fiap.cp02.dto.ReservaDTO;
import com.fiap.cp02.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gerenciamento de reservas de salas")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Criar reserva")
    public ResponseEntity<ReservaDTO> criar(@Valid @RequestBody ReservaDTO dto) {
        ReservaDTO criada = reservaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @Operation(summary = "Listar reservas com paginação")
    public ResponseEntity<Page<ReservaDTO>> listar(
            @PageableDefault(size = 10, sort = "dataHoraInicio") Pageable pageable) {
        return ResponseEntity.ok(reservaService.listar(pageable));
    }

    @GetMapping("/sala/{salaId}")
    @Operation(summary = "Listar reservas de uma sala específica")
    public ResponseEntity<Page<ReservaDTO>> listarPorSala(
            @PathVariable Long salaId,
            @PageableDefault(size = 10, sort = "dataHoraInicio") Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorSala(salaId, pageable));
    }

    @GetMapping("/filtro")
    @Operation(summary = "Filtrar reservas por sala e/ou período")
    public ResponseEntity<Page<ReservaDTO>> filtrar(
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reservaService.filtrar(salaId, inicio, fim, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
