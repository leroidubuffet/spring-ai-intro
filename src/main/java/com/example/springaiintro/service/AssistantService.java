package com.example.springaiintro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

/**
 * Demuestra el uso de Advisors: memoria de conversación + logging.
 * El servicio no sabe nada de historial — los Advisors lo gestionan.
 *
 * API de memoria en Spring AI 1.0.0 GA:
 *   - InMemoryChatMemoryRepository: almacena los mensajes en memoria
 *   - MessageWindowChatMemory: ChatMemory con ventana deslizante de mensajes
 *   - MessageChatMemoryAdvisor.builder(memory): instanciación vía builder
 *   - ChatMemory.CONVERSATION_ID: clave para pasar el conversationId por llamada
 */
@Service
public class AssistantService {

    private final ChatClient chat;

    public AssistantService(ChatClient.Builder builder) {
        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();

        this.chat = builder
                .defaultAdvisors(
                        // Mantiene el historial y lo incluye en cada llamada
                        MessageChatMemoryAdvisor.builder(memory).build(),

                        // Loguea request y response (útil en desarrollo)
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    public String responder(String conversationId, String pregunta) {
        return chat.prompt()
                   .system("Eres un asistente técnico experto en Java y Spring.")
                   .user(pregunta)
                   // En 1.0.0 los params del advisor se pasan vía AdvisorSpec, no advisorParam()
                   .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                   .call()
                   .content();
    }
}
