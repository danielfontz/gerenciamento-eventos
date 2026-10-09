package com.danielfontz.gerenciamento_eventos.exception;

public class EventoNotFoundException extends RuntimeException {

    public EventoNotFoundException(Long id) {
        super("Não foi possível encontrar o evento com id " + id);
    }
}