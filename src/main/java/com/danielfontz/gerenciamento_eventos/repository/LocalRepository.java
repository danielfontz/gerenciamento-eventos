package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalRepository extends JpaRepository<Local, Long> {

    // Consulta personalizada: por cidade
    Page<Local> findByCidadeIgnoreCase(String cidade, Pageable pageable);

    // Consulta personalizada: por nome (contém, case-insensitive)
    Page<Local> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Consulta personalizada: por endereço (contém, case-insensitive)
    Page<Local> findByEnderecoContainingIgnoreCase(String endereco, Pageable pageable);

    // Utilidades
    Optional<Local> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}