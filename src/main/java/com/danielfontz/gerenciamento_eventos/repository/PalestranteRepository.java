package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PalestranteRepository extends JpaRepository<Palestrante, Long> {

    // Consulta personalizada: por nome (contém, case-insensitive)
    Page<Palestrante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Consulta personalizada: por especialidade
    Page<Palestrante> findByEspecialidadeIgnoreCase(String especialidade, Pageable pageable);

    // Utilidades
    Optional<Palestrante> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}