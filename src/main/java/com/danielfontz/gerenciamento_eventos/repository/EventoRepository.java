package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.model.StatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Consultas derivadas
    Page<Evento> findByStatus(StatusEvento status, Pageable pageable);
    Page<Evento> findByLocalCidadeIgnoreCase(String cidade, Pageable pageable);
    Page<Evento> findByDataBetween(LocalDate inicio, LocalDate fim, Pageable pageable);
    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    boolean existsByTituloIgnoreCaseAndData(String titulo, LocalDate data);
}