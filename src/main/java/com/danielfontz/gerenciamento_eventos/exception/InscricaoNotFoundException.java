package com.danielfontz.gerenciamento_eventos.exception;

public class InscricaoNotFoundException extends RuntimeException {

    public InscricaoNotFoundException(Long id) {
        super("Não foi possível encontrar a inscrição com id " + id);
    }
}