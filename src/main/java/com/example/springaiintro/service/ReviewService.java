package com.example.springaiintro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Uso básico de ChatClient: system prompt + user message → String.
 * Demuestra la API fluida de Spring AI en su forma más simple.
 */
@Service
public class ReviewService {

    private final ChatClient chat;

    public ReviewService(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    public String revisar(String codigo) {
        return chat.prompt()
                   .system("Eres un revisor de código experto. Sé conciso.")
                   .user(codigo)
                   .call()
                   .content();
    }
}
