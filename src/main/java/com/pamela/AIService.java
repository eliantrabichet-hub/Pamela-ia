package com.pamela;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final ChatMessageRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Value("${pamela.api.key:}")
    private String apiKey;

    @Value("${pamela.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${pamela.api.model:gpt-4o-mini}")
    private String model;

    public AIService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public String reply(String conversationId, String userMessage) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta PAMELA_API_KEY.");
        }

        repository.save(new ChatMessage(conversationId, "user", userMessage));

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", """
                    Sos Pamela, una asistente de inteligencia artificial especializada en programación.
                    Respondé en español claro y amable. Ayudá a generar código, detectar y corregir errores,
                    explicar conceptos y planificar proyectos completos. Cuando escribas código, indicá el
                    lenguaje y explicá cómo ejecutarlo. Si falta información importante, hacé una pregunta
                    concreta. No afirmes que ejecutaste o probaste código si no lo hiciste.
                    """
        ));

        for (ChatMessage item : repository.findTop30ByConversationIdOrderByCreatedAtAsc(conversationId)) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("role", item.getRole());
            entry.put("content", item.getContent());
            messages.add(entry);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("messages", messages);
        payload.put("temperature", 0.4);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(90))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("La API devolvió HTTP " + response.statusCode());
        }

        JsonNode json = mapper.readTree(response.body());
        JsonNode content = json.path("choices").path(0).path("message").path("content");
        if (content.isMissingNode() || content.isNull()) {
            throw new IllegalStateException("La respuesta de la API no contiene texto.");
        }

        String answer = content.asText();
        repository.save(new ChatMessage(conversationId, "assistant", answer));
        return answer;
    }
}
