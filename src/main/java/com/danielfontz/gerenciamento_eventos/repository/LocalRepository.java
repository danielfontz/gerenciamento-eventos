package com.danielfontz.gerenciamento_eventos.repository;

import com.danielfontz.gerenciamento_eventos.model.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    // Consultas derivadas
    Page<Local> findByCidadeIgnoreCase(String cidade, Pageable pageable);
    Page<Local> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Local> findByEnderecoContainingIgnoreCase(String endereco, Pageable pageable);
    Optional<Local> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}