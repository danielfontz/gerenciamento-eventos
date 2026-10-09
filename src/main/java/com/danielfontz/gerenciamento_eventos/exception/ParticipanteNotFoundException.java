package com.danielfontz.gerenciamento_eventos.exception;

public class ParticipanteNotFoundException extends RuntimeException {

    public ParticipanteNotFoundException(Long id) {
        super("Não foi possível encontrar o participante com id " + id);
    }
}