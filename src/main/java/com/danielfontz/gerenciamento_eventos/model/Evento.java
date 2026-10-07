package com.danielfontz.gerenciamento_eventos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @NotNull(message = "A data do evento é obrigatória")
    @FutureOrPresent(message = "A data deve ser hoje ou no futuro")
    @Column(nullable = false)
    private LocalDate data;

    @NotNull(message = "O status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEvento status;

    // One-to-One: Um evento tem um local
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "local_id", referencedColumnName = "id", unique = true)
    private Local local;

    // One-to-Many: Um evento tem muitas inscrições
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscricao> inscricoes = new HashSet<>();

    // Many-to-Many: Um evento tem muitos palestrantes
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "evento_palestrante",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "palestrante_id")
    )
    private Set<Palestrante> palestrantes = new HashSet<>();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
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
