package com.example.springaiintro.service;

import com.example.springaiintro.exception.BadRequestException;
import com.example.springaiintro.exception.InvalidModelOutputException;
import com.example.springaiintro.exception.TransientLlmException;
import com.example.springaiintro.model.Review;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * Structured output + streaming + reintentos con Resilience4j.
 * Demuestra los tres mecanismos avanzados de Spring AI del notebook.
 */
@Service
public class CodeReviewService {

    private final ChatClient chat;
    private final Validator validator;

    public CodeReviewService(ChatClient.Builder builder, Validator validator) {
        this.chat = builder.build();
        this.validator = validator;
    }

    // --- Structured output + Bean Validation + Retry ---

    @Retry(name = "llm-call")
    public Review revisar(String language, String codigo) {
        String prompt = """
                Revisa el siguiente código %s.
                Identifica problemas reales. No inventes.
                <<CODIGO>>
                %s
                """.formatted(language, codigo);

        Review review;
        try {
            review = chat.prompt()
                         .user(prompt)
                         .call()
                         .entity(Review.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                // 400/401/403 → no tiene sentido reintentar
                throw new BadRequestException(e.getMessage());
            }
            // 5xx o red → Resilience4j reintentará con backoff exponencial
            throw new TransientLlmException(e.getMessage());
        }

        // Siempre validar el output del modelo antes de devolverlo
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        if (!violations.isEmpty()) {
            throw new InvalidModelOutputException(violations.toString());
        }

        return review;
    }

    // --- Streaming ---

    public Flux<String> revisarStream(String language, String codigo) {
        String prompt = """
                Revisa brevemente el siguiente código %s.
                <<CODIGO>>
                %s
                """.formatted(language, codigo);

        return chat.prompt()
                   .user(prompt)
                   .stream()
                   .content();   // Flux<String>, un elemento por token
    }
}
