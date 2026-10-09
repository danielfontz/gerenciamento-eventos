package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.model.StatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Consulta personalizada: por título (contém, case-insensitive)
    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Consulta personalizada: por status
    Page<Evento> findByStatus(StatusEvento status, Pageable pageable);

    // Consulta personalizada: por cidade do local
    Page<Evento> findByLocalCidadeIgnoreCase(String cidade, Pageable pageable);

    // Consulta personalizada: por intervalo de datas
    Page<Evento> findByDataBetween(LocalDate inicio, LocalDate fim, Pageable pageable);

    // Utilidade para validações
    boolean existsByTituloIgnoreCaseAndData(String titulo, LocalDate data);
}