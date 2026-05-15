package com.example.springaiintro.exception;

public class InvalidModelOutputException extends RuntimeException {
    public InvalidModelOutputException(String violations) {
        super("El modelo devolvió un output inválido: " + violations);
    }
}
