package com.danielfontz.gerenciamento_eventos.exception;

public class PalestranteNotFoundException extends RuntimeException {

    public PalestranteNotFoundException(Long id) {
        super("Não foi possível encontrar o palestrante com id " + id);
    }
}