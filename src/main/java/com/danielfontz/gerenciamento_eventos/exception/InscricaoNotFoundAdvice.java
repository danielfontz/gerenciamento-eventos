package com.danielfontz.gerenciamento_eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class InscricaoNotFoundAdvice {

    @ExceptionHandler(InscricaoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String inscricaoNotFoundHandler(InscricaoNotFoundException ex) {
        return ex.getMessage();
    }
}