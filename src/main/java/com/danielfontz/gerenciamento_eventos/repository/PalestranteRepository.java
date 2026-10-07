package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PalestranteRepository extends JpaRepository<Palestrante, Long> {

    // Consultas derivadas
    Optional<Palestrante> findByEmailIgnoreCase(String email);
    Page<Palestrante> findByEspecialidadeIgnoreCase(String especialidade, Pageable pageable);
    Page<Palestrante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    boolean existsByEmailIgnoreCase(String email);
}