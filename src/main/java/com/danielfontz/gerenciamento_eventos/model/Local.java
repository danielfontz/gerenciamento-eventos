package com.danielfontz.gerenciamento_eventos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Local {

    private @Id @GeneratedValue Long id;

    @NotBlank(message = "O nome do local é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200)
    private String endereco;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 80)
    private String cidade;

    // ===== Relacionamento @OneToOne inverso =====
    @OneToOne(mappedBy = "local")
    @JsonIgnore  // evita loop infinito Evento -> Local -> Evento -> ...
    private Evento evento;

    public Local() {}

    public Local(String nome, String endereco, String cidade) {
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
}