package com.example.springaiintro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

/**
 * Demuestra el uso de Advisors: memoria de conversación + logging.
 * El servicio no sabe nada de historial — los Advisors lo gestionan.
 */
@Service
public class AssistantService {

    private final ChatClient chat;

    public AssistantService(ChatClient.Builder builder) {
        ChatMemory memory = new InMemoryChatMemory();

        this.chat = builder
            .defaultAdvisors(
                // Mantiene el historial y lo incluye en cada llamada
                new MessageChatMemoryAdvisor(memory),

                // Loguea request y response (útil en desarrollo)
                new SimpleLoggerAdvisor()
            )
            .build();
    }

    public String responder(String conversationId, String pregunta) {
        return chat.prompt()
                   .system("Eres un asistente técnico experto en Java y Spring.")
                   .user(pregunta)
                   .advisorParam(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                   .call()
                   .content();
    }
}
