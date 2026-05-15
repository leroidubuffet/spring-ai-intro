package com.example.springaiintro.controller;

import com.example.springaiintro.model.ChatRequest;
import com.example.springaiintro.service.AssistantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * POST /assistant
     * Chat con memoria de conversación por conversationId.
     * El historial persiste en memoria mientras la aplicación esté en ejecución.
     *
     * Body: { "conversationId": "user-123", "message": "¿Qué es un bean?" }
     */
    @PostMapping
    public String chat(@RequestBody ChatRequest req) {
        return assistantService.responder(req.conversationId(), req.message());
    }
}
