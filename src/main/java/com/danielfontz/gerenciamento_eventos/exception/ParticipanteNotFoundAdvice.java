package com.danielfontz.gerenciamento_eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ParticipanteNotFoundAdvice {

    @ExceptionHandler(ParticipanteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String participanteNotFoundHandler(ParticipanteNotFoundException ex) {
        return ex.getMessage();
    }
}