package com.danielfontz.gerenciamento_eventos.exception;

public class LocalNotFoundException extends RuntimeException {

    public LocalNotFoundException(Long id) {
        super("Não foi possível encontrar o local com id " + id);
    }
}