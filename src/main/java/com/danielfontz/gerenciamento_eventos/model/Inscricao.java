package com.danielfontz.gerenciamento_eventos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
public class Inscricao {

    private @Id @GeneratedValue Long id;

    @NotNull(message = "A data da inscrição é obrigatória")
    private LocalDateTime dataInscricao;

    @NotBlank(message = "O status da inscrição é obrigatório")
    @Size(max = 30, message = "O status deve ter no máximo 30 caracteres")
    private String status;

    // @ManyToOne: muitas inscrições para um evento
    @NotNull(message = "O evento é obrigatório")
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // @ManyToOne: muitas inscrições para um participante
    @NotNull(message = "O participante é obrigatório")
    @ManyToOne
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    // Construtor padrão (JPA)
    public Inscricao() {}

    // Construtor com campos
    public Inscricao(LocalDateTime dataInscricao, String status, Evento evento, Participante participante) {
        this.dataInscricao = dataInscricao;
        this.status = status;
        this.evento = evento;
        this.participante = participante;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataInscricao() { return dataInscricao; }
    public void setDataInscricao(LocalDateTime dataInscricao) { this.dataInscricao = dataInscricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
}