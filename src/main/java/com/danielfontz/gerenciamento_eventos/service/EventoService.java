package com.danielfontz.gerenciamento_eventos.service;

import com.danielfontz.gerenciamento_eventos.exception.EventoNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.model.StatusEvento;
import com.danielfontz.gerenciamento_eventos.repository.EventoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    // ----- Listar paginado -----
    public Page<Evento> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ----- Buscar por ID -----
    public Evento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(id));
    }

    // ----- Criar -----
    @Transactional
    public Evento criar(Evento evento) {
        if (repository.existsByTituloIgnoreCaseAndData(evento.getTitulo(), evento.getData())) {
            throw new IllegalArgumentException(
                    "Já existe um evento com o título '" + evento.getTitulo() + "' na data " + evento.getData());
        }
        return repository.save(evento);
    }

    // ----- Atualizar ou criar -----
    @Transactional
    public Evento atualizarOuCriar(Long id, Evento novo) {
        return repository.findById(id)
                .map(evento -> {
                    evento.setTitulo(novo.getTitulo());
                    evento.setData(novo.getData());
                    evento.setStatus(novo.getStatus());
                    return repository.save(evento);
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
            throw new EventoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ----- Consultas personalizadas -----
    public Page<Evento> buscarPorTitulo(String titulo, Pageable pageable) {
        return repository.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    public Page<Evento> buscarPorStatus(StatusEvento status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    public Page<Evento> buscarPorCidade(String cidade, Pageable pageable) {
        return repository.findByLocalCidadeIgnoreCase(cidade, pageable);
    }

    public Page<Evento> buscarPorIntervalo(LocalDate inicio, LocalDate fim, Pageable pageable) {
        return repository.findByDataBetween(inicio, fim, pageable);
    }
}