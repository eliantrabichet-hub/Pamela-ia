package com.pamela;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final AIService aiService;
    private final ChatMessageRepository repository;

    public ChatController(AIService aiService, ChatMessageRepository repository) {
        this.aiService = aiService;
        this.repository = repository;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Escribí un mensaje."));
        }

        String conversationId = (request.conversationId() == null || request.conversationId().isBlank())
                ? UUID.randomUUID().toString()
                : request.conversationId();

        try {
            String answer = aiService.reply(conversationId, request.message());
            return ResponseEntity.ok(Map.of(
                    "conversationId", conversationId,
                    "answer", answer
            ));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Pamela no pudo responder. Revisá la configuración de la API y volvé a intentar."
            ));
        }
    }

    @GetMapping("/history/{conversationId}")
    public List<HistoryItem> history(@PathVariable String conversationId) {
        return repository.findTop30ByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(message -> new HistoryItem(message.getRole(), message.getContent()))
                .toList();
    }

    public record ChatRequest(String conversationId, String message) {}
    public record HistoryItem(String role, String content) {}
}
