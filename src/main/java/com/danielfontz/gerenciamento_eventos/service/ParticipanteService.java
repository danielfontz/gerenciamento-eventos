package com.danielfontz.gerenciamento_eventos.service;

import com.danielfontz.gerenciamento_eventos.exception.ParticipanteNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Participante;
import com.danielfontz.gerenciamento_eventos.repository.ParticipanteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository repository;

    public ParticipanteService(ParticipanteRepository repository) {
        this.repository = repository;
    }

    // ----- Listar paginado -----
    public Page<Participante> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ----- Buscar por ID -----
    public Participante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));
    }

    // ----- Criar -----
    @Transactional
    public Participante criar(Participante participante) {
        if (repository.existsByEmailIgnoreCase(participante.getEmail())) {
            throw new IllegalArgumentException("Já existe um participante com o e-mail " + participante.getEmail());
        }
        return repository.save(participante);
    }

    // ----- Atualizar ou criar -----
    @Transactional
    public Participante atualizarOuCriar(Long id, Participante novo) {
        return repository.findById(id)
                .map(participante -> {
                    participante.setNome(novo.getNome());
                    participante.setEmail(novo.getEmail());
                    participante.setTelefone(novo.getTelefone());
                    return repository.save(participante);
                })
                .orElseGet(() -> {
                    novo.setId(id);
                    return repository.save(novo);
                });
    }

    // ----- Deletar -----
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ParticipanteNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ----- Consulta personalizada: buscar por nome (paginado) -----
    public Page<Participante> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }
}