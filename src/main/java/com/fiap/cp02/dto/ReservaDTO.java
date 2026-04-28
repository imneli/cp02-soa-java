package com.fiap.cp02.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReservaDTO {

    private Long id;

    @NotNull(message = "ID da sala é obrigatório")
    private Long salaId;

    private String salaNome;

    @NotNull(message = "Data/hora de início é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data/hora de fim é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraFim;

    @NotBlank(message = "Responsável é obrigatório")
    private String responsavel;

    public ReservaDTO() {}

    public ReservaDTO(Long id, Long salaId, String salaNome, LocalDateTime dataHoraInicio,
                      LocalDateTime dataHoraFim, String responsavel) {
        this.id = id;
        this.salaId = salaId;
        this.salaNome = salaNome;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.responsavel = responsavel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }
    public String getSalaNome() { return salaNome; }
    public void setSalaNome(String salaNome) { this.salaNome = salaNome; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
