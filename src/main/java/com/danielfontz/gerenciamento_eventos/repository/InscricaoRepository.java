package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // Consultas derivadas
    Page<Inscricao> findByEventoId(Long eventoId, Pageable pageable);
    Page<Inscricao> findByParticipanteId(Long participanteId, Pageable pageable);
    Page<Inscricao> findByStatusIgnoreCase(String status, Pageable pageable);
    Page<Inscricao> findByDataInscricaoBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    Optional<Inscricao> findByEventoIdAndParticipanteId(Long eventoId, Long participanteId);
    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);
}