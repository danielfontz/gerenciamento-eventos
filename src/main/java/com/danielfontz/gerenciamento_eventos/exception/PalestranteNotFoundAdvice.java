package com.danielfontz.gerenciamento_eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PalestranteNotFoundAdvice {

    @ExceptionHandler(PalestranteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String palestranteNotFoundHandler(PalestranteNotFoundException ex) {
        return ex.getMessage();
    }
}