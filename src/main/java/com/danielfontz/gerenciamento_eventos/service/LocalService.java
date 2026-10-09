package com.danielfontz.gerenciamento_eventos.service;

import com.danielfontz.gerenciamento_eventos.exception.LocalNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Local;
import com.danielfontz.gerenciamento_eventos.repository.LocalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalService {

    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    // ----- Listar paginado -----
    public Page<Local> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ----- Buscar por ID -----
    public Local buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new LocalNotFoundException(id));
    }

    // ----- Criar -----
    @Transactional
    public Local criar(Local local) {
        if (repository.existsByNomeIgnoreCase(local.getNome())) {
            throw new IllegalArgumentException("Já existe um local com o nome '" + local.getNome() + "'");
        }
        return repository.save(local);
    }

    // ----- Atualizar ou criar -----
    @Transactional
    public Local atualizarOuCriar(Long id, Local novo) {
        return repository.findById(id)
                .map(local -> {
                    local.setNome(novo.getNome());
                    local.setEndereco(novo.getEndereco());
                    local.setCidade(novo.getCidade());
                    return repository.save(local);
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
            throw new LocalNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ----- Consultas personalizadas -----
    public Page<Local> buscarPorCidade(String cidade, Pageable pageable) {
        return repository.findByCidadeIgnoreCase(cidade, pageable);
    }

    public Page<Local> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Page<Local> buscarPorEndereco(String endereco, Pageable pageable) {
        return repository.findByEnderecoContainingIgnoreCase(endereco, pageable);
    }
}