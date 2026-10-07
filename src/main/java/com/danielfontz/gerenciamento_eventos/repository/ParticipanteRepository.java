package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Participante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    // Consultas derivadas
    Optional<Participante> findByEmailIgnoreCase(String email);
    Page<Participante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Participante> findByTelefoneContaining(String telefone, Pageable pageable);
    boolean existsByEmailIgnoreCase(String email);
}