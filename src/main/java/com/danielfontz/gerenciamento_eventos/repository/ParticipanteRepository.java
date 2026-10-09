package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Participante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    // ----- Consultas personalizadas -----

    // Busca paginada por nome (contém, case-insensitive)
    Page<Participante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca paginada por telefone (contém)
    Page<Participante> findByTelefoneContaining(String telefone, Pageable pageable);

    // ----- Utilidades -----

    Optional<Participante> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}