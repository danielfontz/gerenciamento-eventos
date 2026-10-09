package com.danielfontz.gerenciamento_eventos.service;

import com.danielfontz.gerenciamento_eventos.exception.PalestranteNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import com.danielfontz.gerenciamento_eventos.repository.PalestranteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PalestranteService {

    private final PalestranteRepository repository;

    public PalestranteService(PalestranteRepository repository) {
        this.repository = repository;
    }

    // ----- Listar paginado -----
    public Page<Palestrante> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ----- Buscar por ID -----
    public Palestrante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PalestranteNotFoundException(id));
    }

    // ----- Criar -----
    @Transactional
    public Palestrante criar(Palestrante palestrante) {
        if (repository.existsByEmailIgnoreCase(palestrante.getEmail())) {
            throw new IllegalArgumentException(
                    "Já existe um palestrante com o e-mail " + palestrante.getEmail());
        }
        return repository.save(palestrante);
    }

    // ----- Atualizar ou criar -----
    @Transactional
    public Palestrante atualizarOuCriar(Long id, Palestrante novo) {
        return repository.findById(id)
                .map(palestrante -> {
                    palestrante.setNome(novo.getNome());
                    palestrante.setEmail(novo.getEmail());
                    palestrante.setEspecialidade(novo.getEspecialidade());
                    return repository.save(palestrante);
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
            throw new PalestranteNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ----- Consultas personalizadas -----
    public Page<Palestrante> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Page<Palestrante> buscarPorEspecialidade(String especialidade, Pageable pageable) {
        return repository.findByEspecialidadeIgnoreCase(especialidade, pageable);
    }
}