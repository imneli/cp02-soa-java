package com.fiap.cp02.service;

import com.fiap.cp02.dto.SalaDTO;
import com.fiap.cp02.entity.Sala;
import com.fiap.cp02.exception.ResourceNotFoundException;
import com.fiap.cp02.repository.SalaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    void deveCriarSalaComSucesso() {
        SalaDTO dto = new SalaDTO(null, "Sala Inovação", 20, "Bloco B");
        Sala saved = new Sala(1L, "Sala Inovação", 20, "Bloco B");
        when(salaRepository.save(any(Sala.class))).thenReturn(saved);

        SalaDTO result = salaService.criar(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Sala Inovação");
        verify(salaRepository).save(any(Sala.class));
    }

    @Test
    void deveLancarExcecaoQuandoSalaNaoEncontrada() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveAtualizarSalaComSucesso() {
        Sala existente = new Sala(1L, "Old", 5, "A");
        when(salaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(salaRepository.save(any(Sala.class))).thenAnswer(inv -> inv.getArgument(0));

        SalaDTO dto = new SalaDTO(null, "New", 15, "B");
        SalaDTO result = salaService.atualizar(1L, dto);

        assertThat(result.getNome()).isEqualTo("New");
        assertThat(result.getCapacidade()).isEqualTo(15);
    }
}
