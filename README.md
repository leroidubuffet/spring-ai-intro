# Spring AI — Demo (Módulo 2: Bloque 4)

Proyecto del modulo 2 para la demostración de los conceptos del notebook `2 spring_ai_intro.ipynb`.

## Requisitos

- Java 21
- Maven 3.9+
- Variable de entorno `ANTHROPIC_API_KEY`

## Arrancar

```bash
export ANTHROPIC_API_KEY=sk-ant-...
./mvnw spring-boot:run
```

## Endpoints

### `POST /review` — Structured output

Devuelve un `Review` tipado (score 0–10, lista de issues, resumen).

```bash
curl -s -X POST http://localhost:8080/review \
  -H "Content-Type: application/json" \
  -d '{
    "language": "Java",
    "codigo": "public String getUser(int id) { return db.query(\"SELECT * FROM users WHERE id=\" + id); }"
  }' | jq
```

Respuesta esperada:
```json
{
  "score": 2,
  "issues": ["SQL injection en la query", "Concatenación directa de parámetros sin prepared statement"],
  "summary": "El código es vulnerable a SQL injection. Usar PreparedStatement o un ORM."
}
```

---

### `POST /review/stream` — Streaming SSE

La respuesta llega token a token como Server-Sent Events.

```bash
curl -s -X POST http://localhost:8080/review/stream \
  -H "Content-Type: application/json" \
  -d '{
    "language": "Java",
    "codigo": "for(int i=0;i<list.size();i++) { System.out.println(list.get(i)); }"
  }'
```

---

### `POST /assistant` — Chat con memoria

El historial se mantiene por `conversationId` mientras la app esté en ejecución.

```bash
# Primera pregunta
curl -s -X POST http://localhost:8080/assistant \
  -H "Content-Type: application/json" \
  -d '{"conversationId": "demo-1", "message": "¿Qué es un bean en Spring?"}'

# Segunda pregunta — el modelo recuerda el contexto anterior
curl -s -X POST http://localhost:8080/assistant \
  -H "Content-Type: application/json" \
  -d '{"conversationId": "demo-1", "message": "¿Y cómo se diferencia de un componente?"}'
```

---

## Estructura del proyecto

```
spring-ai-intro/
├── pom.xml
└── src/main/
    ├── java/com/example/springaiintro/
    │   ├── SpringAiIntroApplication.java
    │   ├── controller/
    │   │   ├── ReviewController.java       ← /review y /review/stream
    │   │   └── AssistantController.java    ← /assistant
    │   ├── service/
    │   │   ├── ReviewService.java          ← ChatClient básico (.content())
    │   │   ├── CodeReviewService.java      ← .entity() + streaming + @Retry
    │   │   └── AssistantService.java       ← Advisors (memoria + logging)
    │   ├── model/
    │   │   ├── Review.java                 ← Structured output con Bean Validation
    │   │   ├── ReviewRequest.java
    │   │   └── ChatRequest.java
    │   └── exception/
    │       ├── BadRequestException.java
    │       ├── TransientLlmException.java
    │       └── InvalidModelOutputException.java
    └── resources/
        └── application.yml
```

## Conceptos demostrados

| Concepto | Clase |
|---|---|
| `ChatClient` básico | `ReviewService` |
| Structured output `.entity()` | `CodeReviewService` |
| Streaming `.stream().content()` | `CodeReviewService` |
| Advisors (memoria + logging) | `AssistantService` |
| Reintentos `@Retry` Resilience4j | `CodeReviewService` |
| Bean Validation sobre output del modelo | `CodeReviewService` + `Review` |
