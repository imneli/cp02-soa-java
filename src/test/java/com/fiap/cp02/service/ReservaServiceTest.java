package com.fiap.cp02.service;

import com.fiap.cp02.dto.ReservaDTO;
import com.fiap.cp02.entity.Reserva;
import com.fiap.cp02.entity.Sala;
import com.fiap.cp02.exception.ReservaConflictException;
import com.fiap.cp02.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaService salaService;

    @InjectMocks
    private ReservaService reservaService;

    private Sala sala;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    @BeforeEach
    void setUp() {
        sala = new Sala(1L, "Sala A", 10, "Bloco 1");
        inicio = LocalDateTime.now().plusHours(1);
        fim = LocalDateTime.now().plusHours(2);
    }

    @Test
    void deveCriarReservaQuandoNaoHaConflito() {
        ReservaDTO dto = new ReservaDTO(null, 1L, null, inicio, fim, "João");

        when(salaService.findById(1L)).thenReturn(sala);
        when(reservaRepository.existsConflict(eq(1L), any(), any(), eq(-1L))).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReservaDTO result = reservaService.criar(dto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getResponsavel()).isEqualTo("João");
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoQuandoHaConflitoDereserva() {
        ReservaDTO dto = new ReservaDTO(null, 1L, null, inicio, fim, "Maria");

        when(salaService.findById(1L)).thenReturn(sala);
        when(reservaRepository.existsConflict(eq(1L), any(), any(), eq(-1L))).thenReturn(true);

        assertThatThrownBy(() -> reservaService.criar(dto))
                .isInstanceOf(ReservaConflictException.class)
                .hasMessageContaining("Conflito de reserva");

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoFimAntesDeinicio() {
        assertThatThrownBy(() ->
            reservaService.validarIntervalo(fim, inicio)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("posterior ao início");
    }

    @Test
    void deveLancarExcecaoQuandoInicioNoPassado() {
        LocalDateTime passado = LocalDateTime.now().minusHours(1);
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);

        assertThatThrownBy(() ->
            reservaService.validarIntervalo(passado, futuro)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("futura");
    }

    @Test
    void deveLancarExcecaoQuandoDuracaoMenorQue15Minutos() {
        LocalDateTime inicioFuturo = LocalDateTime.now().plusHours(1);
        LocalDateTime fimMuitoCurto = inicioFuturo.plusMinutes(10);

        assertThatThrownBy(() ->
            reservaService.validarIntervalo(inicioFuturo, fimMuitoCurto)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("15 minutos");
    }
}
