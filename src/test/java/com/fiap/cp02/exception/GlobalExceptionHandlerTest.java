package com.fiap.cp02.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar404QuandoRecursoNaoEncontrado() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new ResourceNotFoundException("Sala não encontrada: 99"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody().get("message")).isEqualTo("Sala não encontrada: 99");
    }

    @Test
    void deveRetornar409QuandoConflitoDeReserva() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleConflict(new ReservaConflictException("Conflito de reserva: sala ocupada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("status", 409);
        assertThat(response.getBody().get("message")).isEqualTo("Conflito de reserva: sala ocupada");
    }

    @Test
    void deveRetornar400QuandoArgumentoInvalido() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Data/hora de fim deve ser posterior ao início"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
    }
}
