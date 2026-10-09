package com.danielfontz.gerenciamento_eventos.service;

import com.danielfontz.gerenciamento_eventos.exception.EventoNotFoundException;
import com.danielfontz.gerenciamento_eventos.exception.InscricaoNotFoundException;
import com.danielfontz.gerenciamento_eventos.exception.ParticipanteNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import com.danielfontz.gerenciamento_eventos.repository.EventoRepository;
import com.danielfontz.gerenciamento_eventos.repository.InscricaoRepository;
import com.danielfontz.gerenciamento_eventos.repository.ParticipanteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InscricaoService {

    private final InscricaoRepository repository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoService(InscricaoRepository repository,
                            EventoRepository eventoRepository,
                            ParticipanteRepository participanteRepository) {
        this.repository = repository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    // ----- Listar paginado -----
    public Page<Inscricao> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ----- Buscar por ID -----
    public Inscricao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new InscricaoNotFoundException(id));
    }

    // ----- Criar -----
    @Transactional
    public Inscricao criar(Inscricao inscricao) {
        // 1. Validar existência do evento e participante
        Long eventoId = inscricao.getEvento().getId();
        Long participanteId = inscricao.getParticipante().getId();

        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }
        if (!participanteRepository.existsById(participanteId)) {
            throw new ParticipanteNotFoundException(participanteId);
        }

        // 2. Impedir duplicidade
        if (repository.existsByEventoIdAndParticipanteId(eventoId, participanteId)) {
            throw new IllegalArgumentException(
                    "O participante " + participanteId + " já está inscrito no evento " + eventoId);
        }

        // 3. Se dataInscricao não foi informada, usar agora
        if (inscricao.getDataInscricao() == null) {
            inscricao.setDataInscricao(LocalDateTime.now());
        }

        return repository.save(inscricao);
    }

    // ----- Atualizar ou criar -----
    @Transactional
    public Inscricao atualizarOuCriar(Long id, Inscricao novo) {
        return repository.findById(id)
                .map(inscricao -> {
                    inscricao.setDataInscricao(novo.getDataInscricao());
                    inscricao.setStatus(novo.getStatus());
                    inscricao.setEvento(novo.getEvento());
                    inscricao.setParticipante(novo.getParticipante());
                    return repository.save(inscricao);
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
            throw new InscricaoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ----- Consultas personalizadas -----
    public Page<Inscricao> buscarPorEvento(Long eventoId, Pageable pageable) {
        return repository.findByEventoId(eventoId, pageable);
    }

    public Page<Inscricao> buscarPorParticipante(Long participanteId, Pageable pageable) {
        return repository.findByParticipanteId(participanteId, pageable);
    }

    public Page<Inscricao> buscarPorStatus(String status, Pageable pageable) {
        return repository.findByStatusIgnoreCase(status, pageable);
    }

    public Page<Inscricao> buscarPorIntervalo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return repository.findByDataInscricaoBetween(inicio, fim, pageable);
    }
}