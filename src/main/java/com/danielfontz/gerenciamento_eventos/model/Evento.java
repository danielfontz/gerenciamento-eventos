package com.danielfontz.gerenciamento_eventos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Evento {

    private @Id @GeneratedValue Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @FutureOrPresent(message = "A data deve ser hoje ou no futuro")
    private LocalDate data;

    @NotNull(message = "O status é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusEvento status;

    // ===== @OneToOne com Local =====
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "local_id", referencedColumnName = "id")
    private Local local;

    // ===== @OneToMany com Inscricao =====
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // evita loop: Evento -> Inscricao -> Evento -> ...
    private Set<Inscricao> inscricoes = new HashSet<>();

    // ===== @ManyToMany com Palestrante =====
    @ManyToMany
    @JoinTable(
            name = "evento_palestrante",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "palestrante_id")
    )
    private Set<Palestrante> palestrantes = new HashSet<>();

    public Evento() {}

    public Evento(String titulo, LocalDate data, StatusEvento status) {
        this.titulo = titulo;
        this.data = data;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }
    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }
    public Set<Inscricao> getInscricoes() { return inscricoes; }
    public void setInscricoes(Set<Inscricao> inscricoes) { this.inscricoes = inscricoes; }
    public Set<Palestrante> getPalestrantes() { return palestrantes; }
    public void setPalestrantes(Set<Palestrante> palestrantes) { this.palestrantes = palestrantes; }
}