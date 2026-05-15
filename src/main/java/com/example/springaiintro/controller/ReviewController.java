package com.example.springaiintro.controller;

import com.example.springaiintro.model.Review;
import com.example.springaiintro.model.ReviewRequest;
import com.example.springaiintro.service.CodeReviewService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final CodeReviewService codeReviewService;

    public ReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    /**
     * POST /review
     * Respuesta síncrona con structured output.
     * Devuelve un objeto Review tipado (score, issues, summary).
     */
    @PostMapping
    public Review revisar(@RequestBody ReviewRequest req) {
        return codeReviewService.revisar(req.language(), req.codigo());
    }

    /**
     * POST /review/stream
     * Respuesta en streaming SSE: llega token a token.
     * Útil para interfaces de usuario que muestran la respuesta progresivamente.
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> revisarStream(@RequestBody ReviewRequest req) {
        return codeReviewService.revisarStream(req.language(), req.codigo());
    }
}
