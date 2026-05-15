package com.example.springaiintro.exception;

public class TransientLlmException extends RuntimeException {
    public TransientLlmException(String message) {
        super(message);
    }
}
