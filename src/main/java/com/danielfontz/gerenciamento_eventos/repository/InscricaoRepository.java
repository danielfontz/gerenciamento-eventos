package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // Consulta personalizada: por evento
    Page<Inscricao> findByEventoId(Long eventoId, Pageable pageable);

    // Consulta personalizada: por participante
    Page<Inscricao> findByParticipanteId(Long participanteId, Pageable pageable);

    // Consulta personalizada: por status
    Page<Inscricao> findByStatusIgnoreCase(String status, Pageable pageable);

    // Consulta personalizada: por intervalo de datas
    Page<Inscricao> findByDataInscricaoBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    // Utilidades
    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);
    Optional<Inscricao> findByEventoIdAndParticipanteId(Long eventoId, Long participanteId);
}