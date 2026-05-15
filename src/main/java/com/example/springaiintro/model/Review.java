package com.example.springaiintro.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Structured output del modelo. Spring AI deriva el schema JSON de este record
 * y lo envía como restricción de formato en la llamada.
 */
public record Review(

        @Min(0) @Max(10)
        int score,

        @NotEmpty
        List<String> issues,

        @Size(min = 20, max = 500)
        String summary

) {}
