package com.fiap.cp02.controller;

import com.fiap.cp02.dto.SalaDTO;
import com.fiap.cp02.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@Tag(name = "Salas de Reunião", description = "CRUD de salas de reunião")
@SecurityRequirement(name = "bearerAuth")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @Operation(summary = "Criar sala")
    public ResponseEntity<SalaDTO> criar(@Valid @RequestBody SalaDTO dto) {
        SalaDTO criada = salaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @Operation(summary = "Listar salas com paginação")
    public ResponseEntity<Page<SalaDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(salaService.listar(pageable));
    }

    @GetMapping("/filtro")
    @Operation(summary = "Filtrar salas por capacidade mínima ou localização")
    public ResponseEntity<List<SalaDTO>> filtrar(
            @RequestParam(required = false) Integer capacidadeMinima,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(salaService.filtrar(capacidadeMinima, localizacao));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID")
    public ResponseEntity<SalaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sala")
    public ResponseEntity<SalaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody SalaDTO dto) {
        return ResponseEntity.ok(salaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover sala")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        salaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
